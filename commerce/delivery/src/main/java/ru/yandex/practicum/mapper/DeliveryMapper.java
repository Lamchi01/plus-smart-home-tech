package ru.yandex.practicum.mapper;

import ru.yandex.practicum.model.Address;
import ru.yandex.practicum.model.AddressDto;
import ru.yandex.practicum.model.Delivery;
import ru.yandex.practicum.model.DeliveryDto;

public class DeliveryMapper {
    public static Delivery toDelivery(DeliveryDto deliveryDto) {
        Delivery delivery = new Delivery();
        delivery.setDeliveryId(deliveryDto.getDeliveryId());
        delivery.setDeliveryState(deliveryDto.getDeliveryState());
        delivery.setFromAddress(toAddress(deliveryDto.getFromAddress()));
        delivery.setToAddress(toAddress(deliveryDto.getToAddress()));
        delivery.setOrderId(deliveryDto.getOrderId());
        return delivery;
    }

    public static DeliveryDto toDeliveryDto(Delivery delivery) {
        DeliveryDto deliveryDto = new DeliveryDto();
        deliveryDto.setDeliveryId(delivery.getDeliveryId());
        deliveryDto.setDeliveryState(delivery.getDeliveryState());
        deliveryDto.setFromAddress(toAddressDto(delivery.getFromAddress()));
        deliveryDto.setToAddress(toAddressDto(delivery.getToAddress()));
        deliveryDto.setOrderId(delivery.getOrderId());
        return deliveryDto;
    }

    private static Address toAddress(AddressDto addressDto) {
        Address address = new Address();
        address.setCountry(addressDto.getCountry());
        address.setCity(addressDto.getCity());
        address.setStreet(addressDto.getStreet());
        address.setHouse(addressDto.getHouse());
        address.setFlat(addressDto.getFlat());
        return address;
    }

    private static AddressDto toAddressDto(Address address) {
        AddressDto addressDto = new AddressDto();
        addressDto.setCountry(address.getCountry());
        addressDto.setCity(address.getCity());
        addressDto.setStreet(address.getStreet());
        addressDto.setHouse(address.getHouse());
        addressDto.setFlat(address.getFlat());
        return addressDto;
    }
}