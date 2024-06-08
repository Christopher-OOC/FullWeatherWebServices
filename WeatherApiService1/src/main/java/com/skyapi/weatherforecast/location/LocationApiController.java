package com.skyapi.weatherforecast.location;

import java.net.URI;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

import org.hibernate.validator.constraints.Length;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.IanaLinkRelations;
import org.springframework.hateoas.PagedModel;
import org.springframework.hateoas.PagedModel.PageMetadata;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.skyapi.weatherforecast.BadRequestException;
import com.skyapi.weatherforecast.common.Location;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;

@RestController
@RequestMapping("/v1/locations")
@Validated
public class LocationApiController {

	private LocationService service;

	private ModelMapper modelMapper;

	private Map<String, String> propertyMap = Map.of("code", "code", "city_name", "cityName", "region_name",
			"regionName", "country_code", "countryCode", "country_name", "countryName", "enabled", "enabled");

	public LocationApiController(LocationService service, ModelMapper modelMapper) {
		super();
		this.service = service;
		this.modelMapper = modelMapper;
	}

	@PostMapping
	public ResponseEntity<LocationDto> addLocation(@RequestBody @Valid LocationDto locationDto) {
		Location addedLocation = service.add(dto2Entity(locationDto));
		URI uri = URI.create("/v1/locations/" + addedLocation.getCode());
		
		return ResponseEntity.created(uri).body(entity2Dto(addedLocation));
	}

	@Deprecated
	public ResponseEntity<?> listLocations() {
		List<Location> locations = service.list();

		if (locations.isEmpty()) {
			return ResponseEntity.noContent().build();
		}

		return ResponseEntity.ok(listEntity2ListDto(locations));
	}

	@GetMapping
	public ResponseEntity<?> listLocations(
			@RequestParam(value = "page", required = false, defaultValue = "1") @Min(value = 1) int pageNum,
			@RequestParam(value = "size", required = false, defaultValue = "3") @Min(value = 3) @Max(value = 20) int pageSize,
			@RequestParam(value = "sort", required = false, defaultValue = "code") String sortOption,
			@RequestParam(value = "enabled", required = false, defaultValue = "") String enabled,
			@RequestParam(value = "region_name", required = false, defaultValue = "") String regionName,
			@RequestParam(value = "country_code", required = false, defaultValue = "") String countryCode)
			throws BadRequestException {

		String[] sortFields = sortOption.split(",");
		
		if (sortFields.length > 1) { // sort by multiple fields
			for (int i = 0; i < sortFields.length; i++) {
				String actualFieldName = sortOption.replace("-", "");
				
				if (!propertyMap.containsKey(actualFieldName)) {
					throw new BadRequestException("Invdalid sort field " + actualFieldName);
				}
			}
		} else { // sort by single field
			String actualFieldName = sortOption.replace("-", "");
			
			if (!propertyMap.containsKey(actualFieldName)) {
				throw new BadRequestException("Invalid sort field " + actualFieldName);
			}
			
			sortOption = sortOption.replace(actualFieldName, propertyMap.get(actualFieldName));
		}
		
		
		Map<String, Object> filterFields = new HashMap<>();

		if (!"".equals(enabled)) {
			filterFields.put("enabled", Boolean.parseBoolean(enabled));
		}

		if (!"".equals(regionName)) {
			filterFields.put("regionName", regionName);
		}

		if (!"".equals(countryCode)) {
			filterFields.put("countryCode", countryCode);
		}

		Page<Location> page = service.listByPage(pageNum - 1, pageSize, sortOption, filterFields);

		List<Location> locations = page.getContent();

		if (locations.isEmpty()) {
			return ResponseEntity.noContent().build();
		}

		return ResponseEntity.ok(addPageMetadataAndLinks2Collection(listEntity2ListDto(locations), page, sortOption,
				enabled, regionName, countryCode));
	}

	private CollectionModel<LocationDto> addPageMetadataAndLinks2Collection(List<LocationDto> listDto,
			Page<Location> pageInfo, String sortField, String enabled, String regionName, String countryCode)
			throws BadRequestException {

		String actualEnabled = "".equals(enabled) ? null : enabled;
		String actualRegionName = "".equals(regionName) ? null : regionName;
		String actualCountryCode = "".equals(countryCode) ? null : countryCode;

		// Add self link
		for (var dto : listDto) {
			dto.add(linkTo(methodOn(LocationApiController.class).getLocation(dto.getCode())).withSelfRel());
		}

		int pageSize = pageInfo.getSize();
		int pageNum = pageInfo.getNumber() + 1;
		long totalElements = pageInfo.getTotalElements();
		int totalPages = pageInfo.getTotalPages();

		PageMetadata pageMetadata = new PageMetadata(pageSize, pageNum, totalElements);

		CollectionModel<LocationDto> collectionModel = PagedModel.of(listDto, pageMetadata);

		// add self link to collection
		collectionModel.add(linkTo(methodOn(LocationApiController.class).listLocations(pageNum, pageSize, sortField,
				actualEnabled, actualRegionName, actualCountryCode)).withSelfRel());

		if (pageNum > 1) {
			// add link to first if the current page is not the first one
			collectionModel.add(linkTo(methodOn(LocationApiController.class).listLocations(1, pageSize, sortField,
					actualEnabled, actualRegionName, actualCountryCode)).withRel(IanaLinkRelations.FIRST));

			// add link to the previous page if the current page is not the first one
			collectionModel.add(linkTo(methodOn(LocationApiController.class).listLocations(pageNum - 1, pageSize,
					sortField, actualEnabled, actualRegionName, actualCountryCode)).withRel(IanaLinkRelations.PREV));

		}

		if (pageNum < totalPages) {
			// add link to next page if the current page is not the last one
			collectionModel.add(linkTo(methodOn(LocationApiController.class).listLocations(pageNum + 1, pageSize,
					sortField, actualEnabled, actualRegionName, actualCountryCode)).withRel(IanaLinkRelations.NEXT));

			// add link to last page if the current page is not the last one
			collectionModel.add(linkTo(methodOn(LocationApiController.class).listLocations(totalPages, pageSize,
					sortField, actualEnabled, actualRegionName, actualCountryCode)).withRel(IanaLinkRelations.LAST));
		}

		return collectionModel;
	}

	@GetMapping("/{code}")
	public ResponseEntity<?> getLocation(
			@PathVariable("code") @Length(min = 5, max = 10, message = "code must be between 5-10 characters") String code) {
		Location location = service.get(code);

		return ResponseEntity.ok(entity2Dto(location));
	}

	@PutMapping
	public ResponseEntity<?> updateLocation(@RequestBody @Valid LocationDto location) {
		LocationDto updateLocation = service.update(location);

		return ResponseEntity.ok(updateLocation);
	}

	@DeleteMapping("/{code}")
	public ResponseEntity<?> deleteLocation(@PathVariable("code") String code) {

		service.delete(code);

		return ResponseEntity.noContent().build();

	}

	private List<LocationDto> listEntity2ListDto(List<Location> listEntity) {
		return listEntity.stream().map(entity -> entity2Dto(entity)).collect(Collectors.toList());
	}

	private LocationDto entity2Dto(Location entity) {
		return modelMapper.map(entity, LocationDto.class);
	}

	private Location dto2Entity(LocationDto dto) {
		return modelMapper.map(dto, Location.class);
	}
}
