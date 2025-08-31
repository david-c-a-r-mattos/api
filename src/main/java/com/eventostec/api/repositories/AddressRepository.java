
package com.eventostec.api.repositories;

import com.eventostec.api.domain.address.Address;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;


public interface AddressRepository extends JpaRepository<Address, UUID>{
    
}
