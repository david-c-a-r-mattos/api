package com.eventostec.api.service;

import com.eventostec.api.domain.event.Event;
import com.eventostec.api.domain.address.Address;
import com.eventostec.api.repositories.AddressRepository;
import org.springframework.stereotype.Service;

@Service
public class AddressService {
    
    private final AddressRepository addressRepository;
    
    public AddressService(AddressRepository addressRepository) {
        this.addressRepository = addressRepository;
    }
    
    public void createAddress(String city, String state, Event event) {
        Address address = new Address();
        address.setCity(city);
        address.setUf(state); // UF é equivalente a state
        address.setEvent(event);
        
        addressRepository.save(address);
    }
}