
package com.eventostec.api.domain.address;

import com.eventostec.api.domain.event.Event;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name="address")
@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class Address 
{
    @Id
    @GeneratedValue
    private UUID id;
    private String city;
    private String uf;
    @ManyToOne
    @JoinColumn(name = "event_id")
    private Event event;
}
