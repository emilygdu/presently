package com.presently.user;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import java.util.Collection;
import java.util.Collections;

@Data // lombok generiert die Get- & Set- Methoden
@NoArgsConstructor // lombok generiert den leeren Konstruktor
@AllArgsConstructor // lombok generiert einen Konstruktor mit allen Feldern
@Entity // zeigt das diese Klasse eine Datenbanktabelle ist
@Table(name = "users") //Tabelle heißt in der Datenbank users

public class User implements UserDetails {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true) //nullable= false bedeutet das dieses Feld nicht leer sein darf, unique=true bedeutet das wert einzigartig sein muss
    private String username;

    @Column(nullable = false, unique = true)
    private String email;

    @Column(nullable = false)
    private String password;

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return Collections.emptyList();
    }

    @Override
    public boolean isAccountNonExpired() { return true; }

    @Override
    public boolean isAccountNonLocked() { return true; }

    @Override
    public boolean isCredentialsNonExpired() { return true; }

    @Override
    public boolean isEnabled() { return true; }
 
}
