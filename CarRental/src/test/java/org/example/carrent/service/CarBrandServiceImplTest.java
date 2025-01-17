package org.example.carrent.service;
import org.example.carrent.dto.CarBrandDTO;
import org.example.carrent.entity.CarBrand;
import org.example.carrent.exception.ResourceNotFoundException;
import org.example.carrent.repository.CarBrandRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

public class CarBrandServiceImplTest {

    @Mock
    private CarBrandRepository carBrandRepository;

    @InjectMocks
    private CarBrandServiceImpl carBrandService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testGetAllCarBrands() {
        CarBrand carBrand = new CarBrand();
        carBrand.setId(1L);
        carBrand.setBrand("TestBrand");

        when(carBrandRepository.findAll()).thenReturn(Collections.singletonList(carBrand));

        List<CarBrandDTO> result = carBrandService.getAllCarBrands();

        assertEquals(1, result.size());
        assertEquals("TestBrand", result.get(0).getBrand());
        verify(carBrandRepository, times(1)).findAll();
    }

    @Test
    void testAddCarBrand() {
        CarBrandDTO carBrandDTO = new CarBrandDTO();
        carBrandDTO.setBrand("TestBrand");

        CarBrand carBrand = new CarBrand();
        carBrand.setBrand("TestBrand");

        when(carBrandRepository.save(any(CarBrand.class))).thenReturn(carBrand);

        CarBrandDTO result = carBrandService.addCarBrand(carBrandDTO);

        assertNotNull(result);
        assertEquals("TestBrand", result.getBrand());
        verify(carBrandRepository, times(1)).save(any(CarBrand.class));
    }

    @Test
    void testAddCarBrandWithIdThrowsException() {
        CarBrandDTO carBrandDTO = new CarBrandDTO();
        carBrandDTO.setId(1L);

        assertThrows(IllegalArgumentException.class, () -> carBrandService.addCarBrand(carBrandDTO));
    }

    @Test
    void testFindCarBrandById() {
        Long id = 1L;
        CarBrand carBrand = new CarBrand();
        carBrand.setId(id);
        carBrand.setBrand("TestBrand");

        when(carBrandRepository.findById(id)).thenReturn(Optional.of(carBrand));

        CarBrandDTO result = carBrandService.findCarBrandById(id);

        assertNotNull(result);
        assertEquals("TestBrand", result.getBrand());
        verify(carBrandRepository, times(1)).findById(id);
    }

    @Test
    void testFindCarBrandByIdThrowsException() {
        Long id = 1L;
        when(carBrandRepository.findById(id)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> carBrandService.findCarBrandById(id));
    }
}
