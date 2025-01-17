package org.example.carrent.service;
import org.example.carrent.dto.CarModelDTO;
import org.example.carrent.entity.CarBrand;
import org.example.carrent.entity.CarModel;
import org.example.carrent.exception.ResourceNotFoundException;
import org.example.carrent.mapper.CarModelMapper;
import org.example.carrent.repository.CarModelRepository;
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

public class CarModelServiceImplTest {

    @Mock
    private CarModelRepository carModelRepository;

    @Mock
    private CarModelMapper carModelMapper;

    @InjectMocks
    private CarModelServiceImpl carModelService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testAddCarModel() {
        CarModelDTO carModelDTO = new CarModelDTO();
        carModelDTO.setModel("TestModel");
        carModelDTO.setCarBrandId(1L);

        CarBrand carBrand = new CarBrand();
        carBrand.setId(1L);
        carBrand.setBrand("TestBrand");

        CarModel carModel = new CarModel();
        carModel.setModel("TestModel");
        carModel.setCarBrand(carBrand);

        when(carModelMapper.toEntity(carModelDTO)).thenReturn(carModel);
        when(carModelRepository.save(any(CarModel.class))).thenReturn(carModel);

        CarModelDTO expectedDto = CarModelMapper.toDto(carModel);

        CarModelDTO result = carModelService.addCarModel(carModelDTO);

        assertNotNull(result);
        assertEquals(expectedDto.getModel(), result.getModel());
        assertEquals(expectedDto.getCarBrandId(), result.getCarBrandId());
        assertEquals(expectedDto.getCarBrandName(), result.getCarBrandName());
        verify(carModelRepository, times(1)).save(any(CarModel.class));
        verify(carModelMapper, times(1)).toEntity(carModelDTO);
    }

    @Test
    void testFindCarModelById() {
        Long id = 1L;
        CarBrand carBrand = new CarBrand();
        carBrand.setId(1L);
        carBrand.setBrand("TestBrand");

        CarModel carModel = new CarModel();
        carModel.setId(id);
        carModel.setModel("TestModel");
        carModel.setCarBrand(carBrand);

        when(carModelRepository.findById(id)).thenReturn(Optional.of(carModel));

        CarModelDTO result = carModelService.findCarModelById(id);

        assertNotNull(result);
        assertEquals("TestModel", result.getModel());
        assertEquals(carBrand.getId(), result.getCarBrandId());
        assertEquals(carBrand.getBrand(), result.getCarBrandName());
        verify(carModelRepository, times(1)).findById(id);
    }

    @Test
    void testFindCarModelByIdThrowsException() {
        Long id = 1L;
        when(carModelRepository.findById(id)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> carModelService.findCarModelById(id));
        verify(carModelRepository, times(1)).findById(id);
    }

    @Test
    void testFindAllWithBrandId() {
        Long brandId = 1L;
        CarBrand carBrand = new CarBrand();
        carBrand.setId(brandId);
        carBrand.setBrand("TestBrand");

        CarModel carModel = new CarModel();
        carModel.setCarBrand(carBrand);

        when(carModelRepository.findAllWithBrandId(brandId)).thenReturn(Collections.singletonList(carModel));

        List<CarModelDTO> result = carModelService.findAllWithBrandId(brandId);

        assertEquals(1, result.size());
        assertEquals(CarModelMapper.toDto(carModel).getModel(), result.get(0).getModel());
        verify(carModelRepository, times(1)).findAllWithBrandId(brandId);
    }

    @Test
    void testFindAllWithBrandName() {
        String brandName = "TestBrand";
        CarBrand carBrand = new CarBrand();
        carBrand.setBrand(brandName);

        CarModel carModel = new CarModel();
        carModel.setCarBrand(carBrand);

        when(carModelRepository.findAllWithBrandName(brandName)).thenReturn(Collections.singletonList(carModel));

        List<CarModelDTO> result = carModelService.findAllWithBrandName(brandName);

        assertEquals(1, result.size());
        assertEquals(CarModelMapper.toDto(carModel).getModel(), result.get(0).getModel());
        verify(carModelRepository, times(1)).findAllWithBrandName(brandName);
    }

    @Test
    void testGetAllCarModels() {
        CarBrand carBrand = new CarBrand();
        carBrand.setId(1L);
        carBrand.setBrand("TestBrand");

        CarModel carModel = new CarModel();
        carModel.setCarBrand(carBrand);

        when(carModelRepository.findAll()).thenReturn(Collections.singletonList(carModel));

        List<CarModelDTO> result = carModelService.getAllCarModels();

        assertEquals(1, result.size());
        assertEquals(CarModelMapper.toDto(carModel).getModel(), result.get(0).getModel());
        verify(carModelRepository, times(1)).findAll();
    }
}
