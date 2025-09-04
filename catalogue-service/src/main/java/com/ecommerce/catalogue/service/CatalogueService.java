package com.ecommerce.catalogue.service;

import com.ecommerce.catalogue.dto.ItemRequest;
import com.ecommerce.catalogue.dto.ItemResponse;
import com.ecommerce.catalogue.entity.*;
import com.ecommerce.catalogue.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@Transactional
public class CatalogueService {

    @Autowired
    private ItemRepository itemRepository;

    @Autowired
    private CategoryRepository categoryRepository;

    @Autowired
    private InventoryRepository inventoryRepository;

    @Autowired
    private TaxRateRepository taxRateRepository;

    public Page<ItemResponse> searchItems(String q, Long categoryId, BigDecimal minPrice, 
                                        BigDecimal maxPrice, String color, int page, int size, String sortBy) {
        
        Sort sort = Sort.by(Sort.Direction.ASC, sortBy != null ? sortBy : "name");
        Pageable pageable = PageRequest.of(page, size, sort);
        
        Page<Item> items = itemRepository.findItemsWithFilters(q, categoryId, minPrice, maxPrice, color, pageable);
        
        return items.map(this::convertToItemResponse);
    }

    public ItemResponse getItemById(Long id) {
        Item item = itemRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Item not found with id: " + id));
        
        return convertToItemResponse(item);
    }

    public List<ItemResponse> getItemsByCategory(Long categoryId) {
        List<Item> items = itemRepository.findByCategoryId(categoryId);
        return items.stream()
            .map(this::convertToItemResponse)
            .collect(Collectors.toList());
    }

    public List<Category> getAllCategories() {
        return categoryRepository.findAll();
    }

    public List<String> getAvailableColors() {
        return itemRepository.findDistinctColors();
    }

    public ItemResponse createItem(ItemRequest request) {
        // Validate category exists
        Category category = categoryRepository.findById(request.getCategoryId())
            .orElseThrow(() -> new RuntimeException("Category not found with id: " + request.getCategoryId()));

        // Check if item name already exists in category
        if (itemRepository.existsByNameAndCategoryId(request.getName(), request.getCategoryId())) {
            throw new RuntimeException("Item with name '" + request.getName() + "' already exists in this category");
        }

        // Create item
        Item item = new Item(category, request.getName(), request.getModel(), 
                           request.getPrice(), request.getDimensions(), 
                           request.getColor(), request.getDescription());
        
        item = itemRepository.save(item);

        // Create inventory
        if (request.getStockQty() != null) {
            Inventory inventory = new Inventory(item, request.getStockQty());
            inventoryRepository.save(inventory);
        }

        // Create attributes
        if (request.getAttributes() != null && !request.getAttributes().isEmpty()) {
            for (Map.Entry<String, String> attr : request.getAttributes().entrySet()) {
                ItemAttribute itemAttribute = new ItemAttribute(item, attr.getKey(), attr.getValue());
                // Note: We would need an ItemAttributeRepository to save this
            }
        }

        return convertToItemResponse(item);
    }

    public ItemResponse updateItem(Long id, ItemRequest request) {
        Item item = itemRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Item not found with id: " + id));

        // Update basic fields
        item.setName(request.getName());
        item.setModel(request.getModel());
        item.setPrice(request.getPrice());
        item.setDimensions(request.getDimensions());
        item.setColor(request.getColor());
        item.setDescription(request.getDescription());

        // Update category if changed
        if (!item.getCategory().getId().equals(request.getCategoryId())) {
            Category category = categoryRepository.findById(request.getCategoryId())
                .orElseThrow(() -> new RuntimeException("Category not found with id: " + request.getCategoryId()));
            item.setCategory(category);
        }

        item = itemRepository.save(item);

        // Update inventory
        if (request.getStockQty() != null) {
            Inventory inventory = inventoryRepository.findByItemId(id)
                .orElse(new Inventory(item, 0));
            inventory.setStockQty(request.getStockQty());
            inventoryRepository.save(inventory);
        }

        return convertToItemResponse(item);
    }

    public void deleteItem(Long id) {
        if (!itemRepository.existsById(id)) {
            throw new RuntimeException("Item not found with id: " + id);
        }
        itemRepository.deleteById(id);
    }

    public BigDecimal getGstRate(Long categoryId) {
        return taxRateRepository.findByCategoryId(categoryId)
            .map(TaxRate::getGstPercent)
            .orElse(BigDecimal.ZERO);
    }

    private ItemResponse convertToItemResponse(Item item) {
        ItemResponse response = new ItemResponse();
        response.setId(item.getId());
        response.setName(item.getName());
        response.setModel(item.getModel());
        response.setPrice(item.getPrice());
        response.setDimensions(item.getDimensions());
        response.setColor(item.getColor());
        response.setDescription(item.getDescription());
        response.setCreatedAt(item.getCreatedAt());
        response.setCategoryId(item.getCategory().getId());
        response.setCategoryName(item.getCategory().getName());

        // Set inventory info
        if (item.getInventory() != null) {
            response.setStockQty(item.getInventory().getStockQty());
            response.setInStock(item.getInventory().isInStock());
        } else {
            response.setStockQty(0);
            response.setInStock(false);
        }

        // Set attributes
        if (item.getAttributes() != null && !item.getAttributes().isEmpty()) {
            Map<String, String> attributes = new HashMap<>();
            for (ItemAttribute attr : item.getAttributes()) {
                attributes.put(attr.getAttrKey(), attr.getAttrValue());
            }
            response.setAttributes(attributes);
        }

        // Set GST rate
        response.setGstPercent(getGstRate(item.getCategory().getId()));

        return response;
    }
}
