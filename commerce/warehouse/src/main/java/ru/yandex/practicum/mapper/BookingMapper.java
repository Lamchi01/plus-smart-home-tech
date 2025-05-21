package ru.yandex.practicum.mapper;

import ru.yandex.practicum.model.BookedProductsDto;
import ru.yandex.practicum.model.Booking;
import ru.yandex.practicum.request.AssemblyProductsForOrderRequest;

public class BookingMapper {
    public static Booking toBooking(BookedProductsDto dto, AssemblyProductsForOrderRequest request) {
        Booking booking = new Booking();
        booking.setBookingId(request.getOrderId());
        booking.setOrderId(request.getOrderId());
        booking.setFragile(dto.getFragile());
        booking.setDeliveryVolume(dto.getDeliveryVolume());
        booking.setDeliveryWeight(dto.getDeliveryWeight());
        booking.setProducts(request.getProducts());
        return booking;
    }

    public static BookedProductsDto toBookedProductsDto(Booking booking) {
        BookedProductsDto dto = new BookedProductsDto();
        dto.setDeliveryWeight(booking.getDeliveryWeight());
        dto.setDeliveryVolume(booking.getDeliveryVolume());
        dto.setFragile(booking.getFragile());
        return dto;
    }
}