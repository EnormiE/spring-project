package org.example.carrent.web;

import org.example.carrent.models.VehicleCategoryConfig;
import org.example.carrent.services.VehicleCategoryConfigService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/categories")
public class CategoryController {

    private final VehicleCategoryConfigService configService;

    public CategoryController(VehicleCategoryConfigService configService) {
        this.configService = configService;
    }

    @GetMapping
    public List<VehicleCategoryConfig> list() {
        return configService.findAllCategories();
    }

    @GetMapping("/{category}")
    public VehicleCategoryConfig get(@PathVariable String category) {
        return configService.getByCategory(category);
    }
}