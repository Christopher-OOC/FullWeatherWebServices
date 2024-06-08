package com.skyapi.weatherforecast.location;

import java.util.List;
import java.util.Map;

import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.skyapi.weatherforecast.common.Location;

@Service
@Transactional
public class LocationService {

	private LocationRepository repo;

	private ModelMapper modelMapper;

	public LocationService(LocationRepository repo, ModelMapper modelMapper) {
		super();
		this.repo = repo;
		this.modelMapper = modelMapper;
	}

	public Location add(Location location) {
		return repo.save(location);
	}

	@Deprecated
	public List<Location> list() {
		return repo.findUntrashed();
	}

	@Deprecated
	public Page<Location> listByPage(int pageNum, int pageSize, String sortField) {
		Sort sort = Sort.by(sortField).ascending();
		Pageable pageable = PageRequest.of(pageNum, pageSize, sort);

		return repo.findUntrashed(pageable);
	}

	public Page<Location> listByPage(int pageNum, int pageSize, String sortOption, Map<String, Object> filterFields) {
		String[] sortFields = sortOption.split(",");

		Sort sort = null;

		if (sortFields.length > 1) { // by sorted by multiple fields
			String firstFieldName = sortFields[0];
			String actualFirstFieldName = firstFieldName.replace("-", "");

			sort = firstFieldName.startsWith("-") ? Sort.by(actualFirstFieldName).descending()
					: Sort.by(actualFirstFieldName).ascending();

			for (int i = 1; i < sortFields.length; i++) {
				String nextFieldName = sortFields[i];
				String actualNextFieldName = nextFieldName.replace("-", "");

				sort = sort.and(nextFieldName.startsWith("-") ? Sort.by(actualNextFieldName).descending()
						: Sort.by(actualNextFieldName).ascending());

			}
		
		} else { // by single
			String actualFieldName = sortOption.replace("-", "");
			sort = sortOption.startsWith("-") ? Sort.by(actualFieldName).descending()
					: Sort.by(actualFieldName).ascending();

		}

		Pageable pageable = PageRequest.of(pageNum, pageSize, sort);

		Page<Location> page = repo.listWithFilter(pageable, filterFields);

		if (page.getContent().isEmpty()) {
			throw new LocationNotFoundException("No location found with the given parameters!!!");
		}

		return page;
	}

	public Location get(String code) {

		Location location = repo.findByCode(code);

		if (location == null) {
			throw new LocationNotFoundException(code);
		}

		return location;
	}

	public LocationDto update(LocationDto locationInRequest) {
		String code = locationInRequest.getCode();

		Location locationInDb = repo.findByCode(code);

		if (locationInDb == null) {
			throw new LocationNotFoundException(code);
		}

		locationInDb.setCityName(locationInRequest.getCityName());
		locationInDb.setRegionName(locationInRequest.getRegionName());
		locationInDb.setCountryName(locationInRequest.getCountryName());
		locationInDb.setCountryCode(locationInRequest.getCountryCode());
		locationInDb.setEnabled(locationInRequest.isEnabled());

		repo.save(modelMapper.map(locationInDb, Location.class));

		return locationInRequest;
	}

	public void delete(String code) {
		Location location = repo.findByCode(code);
		if (location == null) {
			throw new LocationNotFoundException(code);
		}

		repo.trashByCode(code);
	}

}
