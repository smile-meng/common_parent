package cn.zm.bos.dao.base;



import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;

import cn.zm.bos.domain.base.Area;

public interface AreaDao extends JpaRepository<Area, String>,JpaSpecificationExecutor<Area>{
	
	@Query(value="select province from Area group by province")
	public List<String> findAllProvince();
	
	@Query(value="select city from Area where province=? group by city")
	public List<String> findCityByProvince(String province);

	@Query(value="from Area where city=?")
	public List<Area> findDistrictByCity(String city);

	public Area findByProvinceAndCityAndDistrict(String province, String city,
			String district);


	
}
