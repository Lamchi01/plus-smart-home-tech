package ru.yandex.practucum.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.yandex.practucum.model.ShoppingCart;
import ru.yandex.practucum.model.ShoppingCartState;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface ShoppingCartRepository extends JpaRepository<ShoppingCart, UUID> {
    Optional<ShoppingCart> findByUsernameAndState(String username, ShoppingCartState state);
}