package org.example.carrent.service;
import org.example.carrent.dto.CarDTO;
import org.example.carrent.entity.Car;
import org.example.carrent.entity.CarBrand;
import org.example.carrent.entity.CarModel;
import org.example.carrent.enums.CarFuelType;
import org.example.carrent.enums.CarState;
import org.example.carrent.enums.CarTransmission;
import org.example.carrent.exception.ResourceNotFoundException;
import org.example.carrent.mapper.CarMapper;
import org.example.carrent.repository.CarRepository;
import org.example.carrent.service.CarServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

public class CarServiceImplTest {

    @Mock
    private CarRepository carRepository;

    @Mock
    private CarMapper carMapper;

    @InjectMocks
    private CarServiceImpl carService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    private Car createTestCar() {
        CarBrand carBrand = new CarBrand();
        carBrand.setId(1L);
        carBrand.setBrand("TestBrand");

        CarModel carModel = new CarModel();
        carModel.setId(1L);
        carModel.setModel("Model S");
        carModel.setCarBrand(carBrand);

        Car car = new Car();
        car.setId(1L);
        car.setCarModel(carModel);
        car.setRegistrationNumber("ABC123");
        car.setState(CarState.AVAILABLE);
        car.setProductionYear(2020);
        car.setColor("Red");
        car.setPricePerDay(100L);
        car.setTransmission(CarTransmission.MANUAL);
        car.setFuelType(CarFuelType.PETROL);
        car.setSeats(5);
        car.setEngineCapacity(2000);

        return car;
    }

    @Test
    void testAddCar() {
        CarDTO carDTO = new CarDTO();
        carDTO.setCarModelId(1L);
        carDTO.setRegistrationNumber("ABC123");
        carDTO.setState(CarState.AVAILABLE);

        Car car = createTestCar();

        when(carMapper.toEntity(carDTO)).thenReturn(car);
        when(carRepository.save(any(Car.class))).thenReturn(car);

        CarDTO result = carService.addCar(carDTO);

        assertNotNull(result);
        assertEquals("ABC123", result.getRegistrationNumber());
        verify(carRepository, times(1)).save(any(Car.class));
        verify(carMapper, times(1)).toEntity(carDTO);
    }

    @Test
    void testFindCarById() {
        Long id = 1L;
        Car car = createTestCar();

        when(carRepository.findById(id)).thenReturn(Optional.of(car));

        CarDTO result = carService.findCarById(id);

        assertNotNull(result);
        assertEquals("ABC123", result.getRegistrationNumber());
        verify(carRepository, times(1)).findById(id);
    }

    @Test
    void testFindCarByIdThrowsException() {
        Long id = 1L;
        when(carRepository.findById(id)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> carService.findCarById(id));
        verify(carRepository, times(1)).findById(id);
    }

    @Test
    void testGetAllCars() {
        Car car = createTestCar();

        when(carRepository.findAll()).thenReturn(Collections.singletonList(car));

        List<CarDTO> result = carService.getAllCars();

        assertEquals(1, result.size());
        assertEquals("ABC123", result.get(0).getRegistrationNumber());
        verify(carRepository, times(1)).findAll();
    }

    @Test
    void testFindAvailableCars() {
        LocalDate startDate = LocalDate.now();
        LocalDate endDate = startDate.plusDays(5);
        Car car = createTestCar();

        when(carRepository.findAvailableCarsInDateRange(startDate, endDate)).thenReturn(Collections.singletonList(car));

        List<CarDTO> result = carService.findAvailableCars(startDate, endDate);

        assertEquals(1, result.size());
        assertEquals("ABC123", result.get(0).getRegistrationNumber());
        verify(carRepository, times(1)).findAvailableCarsInDateRange(startDate, endDate);
    }

    @Test
    void testCheckOneCarAvailability() {
        LocalDate startDate = LocalDate.now();
        LocalDate endDate = startDate.plusDays(5);
        Long carId = 1L;

        when(carRepository.checkOneCarAvailability(startDate, endDate, carId)).thenReturn(0);

        Integer result = carService.checkOneCarAvailability(startDate, endDate, carId);

        assertEquals(0, result);
        verify(carRepository, times(1)).checkOneCarAvailability(startDate, endDate, carId);
    }

    @Test
    void testBlockCar() {
        Long id = 1L;
        Car car = createTestCar();

        when(carRepository.findById(id)).thenReturn(Optional.of(car));
        when(carRepository.save(any(Car.class))).thenReturn(car);

        CarDTO result = carService.blockCar(id);

        assertNotNull(result);
        assertEquals(CarState.BLOCKED, car.getState());
        verify(carRepository, times(1)).findById(id);
        verify(carRepository, times(1)).save(car);
    }

    @Test
    void testUnlockCar() {
        Long id = 1L;
        Car car = createTestCar();
        car.setState(CarState.BLOCKED);

        when(carRepository.findById(id)).thenReturn(Optional.of(car));
        when(carRepository.save(any(Car.class))).thenReturn(car);

        CarDTO result = carService.unlockCar(id);

        assertNotNull(result);
        assertEquals(CarState.AVAILABLE, car.getState());
        verify(carRepository, times(1)).findById(id);
        verify(carRepository, times(1)).save(car);
    }

    @Test
    void testDeleteCar() {
        Long id = 1L;

        carService.deleteCar(id);

        verify(carRepository, times(1)).deleteById(id);
    }
}