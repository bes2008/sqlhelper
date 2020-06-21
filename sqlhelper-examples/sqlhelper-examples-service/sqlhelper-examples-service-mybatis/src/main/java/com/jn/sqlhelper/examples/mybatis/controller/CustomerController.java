package com.jn.sqlhelper.examples.mybatis.mbg.controller;

import com.jn.easyjson.core.JSONBuilderProvider;
import com.jn.langx.util.Dates;
import com.jn.sqlhelper.dialect.pagination.PagingRequest;
import com.jn.sqlhelper.dialect.pagination.PagingResult;
import com.jn.sqlhelper.dialect.pagination.SqlPaginations;
import com.jn.sqlhelper.examples.mybatis.mbg.dao.CustomerDao;
import com.jn.sqlhelper.examples.model.Customer;
import io.swagger.annotations.Api;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.propertyeditors.CustomDateEditor;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.*;

import java.util.Date;
import java.util.List;

@Api
@RestController
@RequestMapping("/customers")
public class CustomerController {

    private CustomerDao customerDao;

    @PostMapping
    public void add(Customer customer) {
        customerDao.insert(customer);
    }

    @PutMapping("/{id}")
    public void update(String id, Customer customer) {
        customer.setId(id);
        Customer u = customerDao.selectById(id);
        if (u == null) {
            add(customer);
        } else {
            customerDao.updateById(customer);
        }
    }

    @DeleteMapping("/{id}")
    public void deleteById(@RequestParam("id") String id) {
        customerDao.deleteById(id);
    }

    @InitBinder
    protected void initBinder(WebDataBinder binder) {
        binder.registerCustomEditor(Date.class, new CustomDateEditor(Dates.getSimpleDateFormat("yyyy-MM-dd"), true));
    }

    @GetMapping("/_useMyBatis")
    public PagingResult list_useMyBatis(
            @RequestParam(name = "pageNo", required = false) Integer pageNo,
            @RequestParam(name = "pageSize", required = false) Integer pageSize,
            @RequestParam(name = "sort", required = false) String sort,
            @RequestParam(value = "count", required = false) boolean count,
            @RequestParam(value = "useLastPageIfPageOut", required = false) boolean useLastPageIfPageOut,
            @RequestParam(value = "namelike", required = false) String namelike,
            @RequestParam(value = "birthDate", required = false) Date birthDate,
            @RequestParam(value = "updateDate", required = false) Date updateDate) {
        Customer queryCondition = new Customer();
        queryCondition.setName(namelike);
        queryCondition.setBirthDate(birthDate);
        queryCondition.setUpdateDate(updateDate);

        PagingRequest request = SqlPaginations.preparePagination(pageNo == null ? 1 : pageNo, pageSize == null ? -1 : pageSize, sort);
        request.setEscapeLikeParameter(true);
        request.setCount(count);
        request.setUseLastPageIfPageOut(useLastPageIfPageOut);
        List<Customer> users = customerDao.selectByLimit(queryCondition);
        String json = JSONBuilderProvider.simplest().toJson(request.getResult());
        System.out.println(json);
        json = JSONBuilderProvider.simplest().toJson(users);
        System.out.println(json);
        return request.getResult();
    }


    @Autowired
    public void setCustomerDao(CustomerDao customerDao) {
        this.customerDao = customerDao;
    }
}
