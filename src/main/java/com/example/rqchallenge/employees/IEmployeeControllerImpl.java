package com.example.rqchallenge.employees;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/employee")
public class IEmployeeControllerImpl implements IEmployeeController {
    private String uri = "https://dummy.restapiexample.com/employee";

    @GetMapping("/all")
    @Override
    public ResponseEntity<List<Employee>> getAllEmployees() throws IOException {
        String uri = "https://dummy.restapiexample.com/api/v1/employees";
        RestTemplate restTemplate = new RestTemplate();
        String result = restTemplate.getForObject(uri, String.class);
        JSONObject jsonObject = new JSONObject(result);
        JSONArray employees = (JSONArray) jsonObject.get("data");
        ArrayList<Employee> employeeList = new ArrayList<>();
        try {
            for (int i = 0; i < employees.length(); i++) {
                employeeList.add(JSONConverUtils.convertJsonToEmployee(employees.getJSONObject(i)));
            }
        } catch (JSONException e) {
        }

        ResponseEntity<List<Employee>> resp = new ResponseEntity<>(employeeList, HttpStatus.OK);

        return resp;
    }

    //description - this should return all employees whose name contains or matches the string input provided
    @Override
    public ResponseEntity<List<Employee>> getEmployeesByNameSearch(String searchString) {
        ResponseEntity<List<Employee>> response = null;
        try {
            response = getAllEmployees();
            if (response.getStatusCode().equals(HttpStatus.OK)) {
                List<Employee> employeeList = response.getBody().stream().
                        filter(employee -> employee.getEmployeeName().matches(searchString))
                        .collect(Collectors.toList());
                ResponseEntity<List<Employee>> resp = new ResponseEntity<>(employeeList, HttpStatus.OK);
                return resp;


            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        return null;
    }

    @Override
    public ResponseEntity<Employee> getEmployeeById(String id) {
        RestTemplate restTemplate = new RestTemplate();
        uri = "https://dummy.restapiexample.com/api/v1/employee/" + id;
        String result = restTemplate.getForObject(uri, String.class);
        JSONObject jsonObject = new JSONObject(result);
        return new ResponseEntity<>(JSONConverUtils.convertJsonToEmployee(jsonObject.getJSONObject("data")), HttpStatus.OK);
    }

    @Override
    public ResponseEntity<Integer> getHighestSalaryOfEmployees() {

        try {
            ResponseEntity<List<Employee>> response = getAllEmployees();
            if (response.getStatusCode().equals(HttpStatus.OK)) {
                List<Employee> employeeList = response.getBody();
                OptionalInt maxSal = employeeList.stream().mapToInt(Employee::getEmployeeSalary)
                        .max();
                ResponseEntity<Integer> resp = new ResponseEntity(maxSal.getAsInt(), HttpStatus.OK);

                return resp;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public ResponseEntity<List<String>> getTopTenHighestEarningEmployeeNames() {

        try {
            ResponseEntity<List<Employee>> response = getAllEmployees();
            if (response.getStatusCode().equals(HttpStatus.OK)) {
                final List<String> topSalaries = new ArrayList<>();
                List<Employee> employeeList = response.getBody();
                employeeList.stream().
                        sorted(Comparator.comparingDouble(Employee::getEmployeeSalary).reversed())
                        .limit(10).forEach(employee -> topSalaries.add("" + employee.getEmployeeSalary()));
                ResponseEntity<List<String>> resp = new ResponseEntity(topSalaries, HttpStatus.OK);
                return resp;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public ResponseEntity<Employee> createEmployee(Map<String, Object> employeeInput) {
        uri = "https://dummy.restapiexample.com/api/v1/create";

        Employee employee = new Employee((Integer) employeeInput.get("id"),
                (String) employeeInput.get("employeeName"),
                (Integer) employeeInput.get("employeeSalary"),
                (Integer) employeeInput.get("employeeAge"),
                (String) employeeInput.get("profileImage")
        );


        RestTemplate restTemplate = new RestTemplate();
        String result =
                restTemplate.postForObject(uri, employee, String.class);
        return new ResponseEntity<>(employee, HttpStatus.OK);

    }

    @Override
    public ResponseEntity<String> deleteEmployeeById(String id) {

        RestTemplate restTemplate = new RestTemplate();
        uri = "https://dummy.restapiexample.com/api/v1/delete/" + id;
        ResponseEntity<Employee> response = getEmployeeById(id);
        if (response.getStatusCode().equals(HttpStatus.OK)) {
            restTemplate.delete(uri);
            return new ResponseEntity<String>(response.getBody().getEmployeeName(), HttpStatus.OK);
        }

        return null;
    }
}
