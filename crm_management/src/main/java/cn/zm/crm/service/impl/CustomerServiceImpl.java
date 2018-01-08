package cn.zm.crm.service.impl;

import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import cn.zm.crm.dao.CustomerDao;
import cn.zm.crm.domain.Customer;
import cn.zm.crm.service.CustomerService;

@Service
@Transactional
public class CustomerServiceImpl implements CustomerService{
	
	@Autowired
	private CustomerDao customerDao;
	
	@Override
	public List<Customer> findNoAssociationCustomer() {
		return customerDao.findByFixedAreaIdIsNull();
	}

	@Override
	public List<Customer> findHasAssociationFixedAreaCustomer(String fixedAreaId) {
		return customerDao.findByFixedAreaId(fixedAreaId);
	}

	@Override
	public void associationCustomerToFixedArea(String customerIdStr,
			String fixedAreaId) {
		
		
		customerDao.clearAssociationFixedAreaId(fixedAreaId);
		
		//切割字符串
		if(StringUtils.isBlank(customerIdStr)||"null".equals(customerIdStr)){
			return;
		}
		String[] customerIdArray = customerIdStr.split(",");
		for (String idStr : customerIdArray) {
			Integer id = Integer.parseInt(idStr);
			customerDao.updateFixedAreaId(fixedAreaId,id);
		}
	}

	@Override
	public void regist(Customer customer) {
		customerDao.save(customer);
	}

	@Override
	public Customer findByTelephone(String telephone) {
		
		return customerDao.findByTelephone(telephone);
	}

	@Override
	public void updateType(String telephone) {
		customerDao.updateType(telephone);
	}

	@Override
	public Customer findByTelephoneAndPassword(String telephone, String password) {
		return customerDao.findByTelephoneAndPassword(telephone,password);
	}

	@Override
	public String findFindAreaIdByAddress(String address) {
		return customerDao.findFindAreaIdByAddress(address);
	}

	
}
