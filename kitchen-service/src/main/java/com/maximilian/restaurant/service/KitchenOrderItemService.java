package com.maximilian.restaurant.service;

import com.maximilian.restaurant.data.Coordinates;
import com.maximilian.restaurant.data.KitchenItemDetails;
import com.maximilian.restaurant.entity.KitchenItem;
import com.maximilian.restaurant.entity.KitchenOrder;
import com.maximilian.restaurant.entity.KitchenOrderItemLink;
import com.maximilian.restaurant.entity.KitchenOrderItemPrimaryKey;
import com.maximilian.restaurant.entity.KitchenOrderStatus;
import com.maximilian.restaurant.repository.KitchenItemRepository;
import com.maximilian.restaurant.repository.KitchenOrderItemLinkRepository;
import com.maximilian.restaurant.repository.KitchenOrderRepository;
import com.maximilian.restaurant.request.kitchen.KitchenOrderRequest;
import com.maximilian.restaurant.response.kitchen.KitchenOrderResponse;
import com.maximilian.restaurant.rest.exception.GeneralException;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@Transactional
public class KitchenOrderItemService extends BaseLoggableService {

    private final KitchenOrderRepository kitchenOrderRepository;
    private final KitchenOrderItemLinkRepository kitchenOrderItemLinkRepository;
    private final KitchenItemRepository kitchenItemRepository;

    protected KitchenOrderItemService(KitchenOrderRepository kitchenOrderRepository, KitchenOrderItemLinkRepository kitchenOrderItemLinkRepository, KitchenItemRepository kitchenItemRepository) {
        super(LoggerFactory.getLogger(KitchenOrderItemService.class));
        this.kitchenOrderRepository = kitchenOrderRepository;
        this.kitchenOrderItemLinkRepository = kitchenOrderItemLinkRepository;
        this.kitchenItemRepository = kitchenItemRepository;
        initItems();
    }

    private void initItems() {
        KitchenItem item = new KitchenItem();
        item.setCost(new BigDecimal("25.99"));
        item.setName("Hot dog");
        kitchenItemRepository.save(item);
    }

    public KitchenOrderResponse createKitchenOrderAndConvert(KitchenOrderRequest request) {
        return convertToResponse(createKitchenOrder(request));
    }

    public KitchenOrder createKitchenOrder(KitchenOrderRequest request) {
        KitchenOrder order = new KitchenOrder();
        order.setStatus(KitchenOrderStatus.WAITING_FOR_APPROVAL);
        order.setLatitude(request.getDeliveryPoint().getLatitude());
        order.setLongitude(request.getDeliveryPoint().getLongitude());
        order.setOuterOrderId(request.getOrderId());
        //todo validate
        order = kitchenOrderRepository.save(order);

        Map<Long, Integer> quantityByItemIdMap = new HashMap<>();
        List<KitchenItem> items = request.getItems().stream().map(i -> {
            quantityByItemIdMap.put(i.getItemId(), i.getQuantity());
            return getItemById(i.getItemId());
        }).collect(Collectors.toList());

        order.setOrderItems(new ArrayList<>(items.size()));

        for (KitchenItem item : items) {
            KitchenOrderItemLink link = new KitchenOrderItemLink();

            KitchenOrderItemPrimaryKey key = new KitchenOrderItemPrimaryKey();
            key.setItemId(item.getId());
            key.setOrderId(order.getId());

            link.setId(key);
            link.setQuantity(quantityByItemIdMap.get(item.getId()));
            link.setKitchenItem(item);
            link.setKitchenOrder(order);

            order.getOrderItems().add(link);
        }
        order.getOrderItems().forEach(kitchenOrderItemLinkRepository::save);

        return kitchenOrderRepository.getById(order.getId());
    }

    public KitchenItem getItemById(Long id) {
        return kitchenItemRepository.findById(id)
                .orElseThrow(() -> new GeneralException("Item with id #" + id + " not found"));
    }

    public KitchenOrderResponse getKitchenOrderByIdConverted(Long id) {
        return convertToResponse(getKitchenOrderById(id));
    }

    public KitchenOrder getKitchenOrderById(Long id) {
        return kitchenOrderRepository.findById(id)
                .orElseThrow(() -> new GeneralException("Order with id #" + id + " not found"));
    }

    public KitchenOrderResponse getKitchenOrderByOuterIdConverted(Long id) {
        return convertToResponse(getKitchenOrderByOuterId(id));
    }

    public KitchenOrder getKitchenOrderByOuterId(Long id) {
        return kitchenOrderRepository.findKitchenOrderByOuterOrderId(id)
                .orElseThrow(() -> new GeneralException("Order with outer order id #" + id + " not found"));
    }

    public KitchenOrderResponse rejectKitchenOrderConverted(Long id) {
        return convertToResponse(setKitchenOrderStatus(getKitchenOrderById(id), KitchenOrderStatus.REJECTED));
    }

    public KitchenOrderResponse approveKitchenOrderConverted(Long id) {
        return convertToResponse(setKitchenOrderStatus(getKitchenOrderById(id), KitchenOrderStatus.APPROVED));
    }

    public KitchenOrderResponse rejectKitchenOrderByOuterIdConverted(Long id) {
        return convertToResponse(setKitchenOrderStatus(getKitchenOrderByOuterId(id), KitchenOrderStatus.REJECTED));
    }

    public KitchenOrderResponse approveKitchenOrderByOuterIdConverted(Long id) {
        return convertToResponse(setKitchenOrderStatus(getKitchenOrderByOuterId(id), KitchenOrderStatus.APPROVED));
    }

    public KitchenOrder setKitchenOrderStatus(KitchenOrder kitchenOrderById, KitchenOrderStatus status) {
        kitchenOrderById.setStatus(status);
        //todo validate
        return kitchenOrderRepository.saveAndFlush(kitchenOrderById);
    }

    protected KitchenOrderResponse convertToResponse(KitchenOrder order) {
        KitchenOrderResponse response = new KitchenOrderResponse();
        response.setKitchenOrderId(order.getId());
        response.setStatus(order.getStatus().toString());

        Coordinates coords = new Coordinates();
        coords.setLatitude(order.getLatitude());
        coords.setLongitude(order.getLongitude());
        response.setDeliveryPoint(coords);

        response.setKitchenItemDetails(order.getOrderItems().stream().map(link -> {
            KitchenItemDetails detail = new KitchenItemDetails();
            detail.setItemId(link.getKitchenItem().getId());
            detail.setName(link.getKitchenItem().getName());
            detail.setPrice(link.getKitchenItem().getCost());
            detail.setQuantity(link.getQuantity());
            return detail;
        }).collect(Collectors.toList()));
        return response;
    }

}
