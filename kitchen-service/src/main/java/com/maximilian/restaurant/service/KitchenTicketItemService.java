package com.maximilian.restaurant.service;

import com.maximilian.restaurant.data.Coordinates;
import com.maximilian.restaurant.data.KitchenItemDetails;
import com.maximilian.restaurant.entity.KitchenItem;
import com.maximilian.restaurant.entity.KitchenTicket;
import com.maximilian.restaurant.entity.KitchenTicketItemLink;
import com.maximilian.restaurant.entity.KitchenTicketItemPrimaryKey;
import com.maximilian.restaurant.entity.KitchenTicketStatus;
import com.maximilian.restaurant.repository.KitchenItemRepository;
import com.maximilian.restaurant.repository.KitchenTicketItemLinkRepository;
import com.maximilian.restaurant.repository.KitchenTicketRepository;
import com.maximilian.restaurant.request.kitchen.KitchenItemRequest;
import com.maximilian.restaurant.request.kitchen.KitchenTicketRequest;
import com.maximilian.restaurant.response.kitchen.KitchenTicketResponse;
import com.maximilian.restaurant.rest.exception.GeneralException;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.validation.Validator;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@Transactional
public class KitchenTicketItemService extends BaseLoggableService {

    private final KitchenTicketRepository kitchenTicketRepository;
    private final KitchenTicketItemLinkRepository kitchenTicketItemLinkRepository;
    private final KitchenItemRepository kitchenItemRepository;
    private final Validator validator;

    @Autowired
    protected KitchenTicketItemService(KitchenTicketRepository kitchenTicketRepository, KitchenTicketItemLinkRepository kitchenTicketItemLinkRepository, KitchenItemRepository kitchenItemRepository, Validator validator) {
        super(LoggerFactory.getLogger(KitchenTicketItemService.class));
        this.kitchenTicketRepository = kitchenTicketRepository;
        this.kitchenTicketItemLinkRepository = kitchenTicketItemLinkRepository;
        this.kitchenItemRepository = kitchenItemRepository;
        this.validator = validator;
        initItems();
    }

    private void initItems() {
        KitchenItem item = new KitchenItem();
        item.setCost(new BigDecimal("25.99"));
        item.setName("Hot dog");
        kitchenItemRepository.save(item);
    }

    public KitchenTicketResponse createKitchenTicketAndConvert(KitchenTicketRequest request) {
        return convertToResponse(createKitchenTicket(request));
    }

    public KitchenTicket createKitchenTicket(KitchenTicketRequest request) {


        KitchenTicket ticket = new KitchenTicket();
        ticket.setStatus(KitchenTicketStatus.WAITING_FOR_APPROVAL);
        ticket.setLatitude(request.getDeliveryPoint().getLatitude());
        ticket.setLongitude(request.getDeliveryPoint().getLongitude());
        ticket.setOuterOrderId(request.getOrderId());

        validate(ticket, validator);

        if(kitchenTicketRepository.existsById(request.getOrderId())) {
            throw new GeneralException("Ticket for this order already exists");
        }
        ticket = kitchenTicketRepository.save(ticket);

        Map<Long, Integer> quantityByItemIdMap = new HashMap<>();
        List<KitchenItem> items = request.getItems().stream().map(i -> {
            quantityByItemIdMap.put(i.getItemId(), i.getQuantity());
            return getItemById(i.getItemId());
        }).collect(Collectors.toList());

        ticket.setTicketItems(new ArrayList<>(items.size()));

        for (KitchenItem item : items) {
            KitchenTicketItemLink link = new KitchenTicketItemLink();

            KitchenTicketItemPrimaryKey key = new KitchenTicketItemPrimaryKey();
            key.setItemId(item.getId());
            key.setOrderId(ticket.getOuterOrderId());

            link.setId(key);
            link.setQuantity(quantityByItemIdMap.get(item.getId()));
            link.setKitchenItem(item);
            link.setKitchenTicket(ticket);

            ticket.getTicketItems().add(link);
        }
        ticket.getTicketItems().forEach(kitchenTicketItemLinkRepository::save);

        return kitchenTicketRepository.getById(ticket.getOuterOrderId());
    }

    public KitchenTicketResponse getKitchenTicketByIdConverted(Long id) {
        return convertToResponse(getKitchenTicketById(id));
    }

    public KitchenTicket getKitchenTicketById(Long id) {
        return kitchenTicketRepository.findById(id)
                .orElseThrow(() -> new GeneralException("Ticket with id #" + id + " not found", HttpStatus.NOT_FOUND));
    }

    public KitchenTicketResponse rejectKitchenTicketConverted(Long id) {
        return convertToResponse(setKitchenTicketStatus(getKitchenTicketById(id), KitchenTicketStatus.REJECTED));
    }

    public KitchenTicketResponse approveKitchenTicketConverted(Long id) {
        return convertToResponse(setKitchenTicketStatus(getKitchenTicketById(id), KitchenTicketStatus.APPROVED));
    }

    public KitchenTicket setKitchenTicketStatus(KitchenTicket kitchenTicketById, KitchenTicketStatus status) {
        kitchenTicketById.setStatus(status);

        validate(kitchenTicketById, validator);

        return kitchenTicketRepository.saveAndFlush(kitchenTicketById);
    }

    public KitchenItem getItemById(Long id) {
        return kitchenItemRepository.findById(id)
                .orElseThrow(() -> new GeneralException("Item with id #" + id + " not found", HttpStatus.NOT_FOUND));
    }

    public KitchenItem createItem(KitchenItemRequest request) {
        KitchenItem item = new KitchenItem();
        return updateItem(item, request);
    }

    public KitchenItem updateItem(Long id, KitchenItemRequest request) {
        KitchenItem item = getItemById(id);
        return updateItem(item, request);
    }

    protected KitchenItem updateItem(KitchenItem item, KitchenItemRequest request) {
        item.setName(request.getName());
        item.setCost(request.getCost());

        validate(item, validator);

        return kitchenItemRepository.saveAndFlush(item);
    }

    protected KitchenTicketResponse convertToResponse(KitchenTicket ticket) {
        KitchenTicketResponse response = new KitchenTicketResponse();
        response.setTicketId(ticket.getOuterOrderId());
        response.setStatus(ticket.getStatus().toString());

        Coordinates coords = new Coordinates();
        coords.setLatitude(ticket.getLatitude());
        coords.setLongitude(ticket.getLongitude());
        response.setDeliveryPoint(coords);

        response.setKitchenItemDetails(ticket.getTicketItems().stream().map(link -> {
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
