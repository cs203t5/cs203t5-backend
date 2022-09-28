package com.example.Vox.Viridis.controller;

import java.util.ArrayList;
import java.util.List;

import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@CrossOrigin(origins = "http://localhost:3000")
public class VoxViridisController {

    public VoxViridisController() {}

    @GetMapping("/books")
    public List<String> getBooks() {
        List<String> list = new ArrayList<>();
        list.add("okk");
        list.add("bleh bleh");
        list.add("bleh bleh");
        return list;
    }

    @GetMapping()
    public String helloworld() {
        // System.out.println("okkk");
        return "CS203 Backend";
    }

}
