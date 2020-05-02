package com.jpabook.jpashop.api;

import com.jpabook.jpashop.domain.Address;
import com.jpabook.jpashop.domain.Order;
import com.jpabook.jpashop.domain.OrderStatus;
import com.jpabook.jpashop.repository.OrderRepository;
import com.jpabook.jpashop.repository.OrderSearch;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/* Order
*  Order -> Member
*  Order -> Delivery
*
*  XXToOne 관계의 성능 최적화(ManyToOnem OneToOne*/
@RestController
@RequiredArgsConstructor
public class OrderSimpleApiController {

    private final OrderRepository orderRepository;

    //Order에 갔더니 연관관계인 Member로 감
    //Member에 갔더니 다시 또 연관관계 Order로 감 == 무한루프프
    //해결하려면 양방향 연관관계 매핑된 필드에 대해 전부 @JsonIgnore를 해줘야한다.

    //무한루프는 해결 됐지만, com.fasterxml.jackson.databind.exc.InvalidDefinitionException: No serializer found for class org.hibernate.proxy.pojo.bytebuddy.ByteBuddyInterceptor and no properties discovered to create BeanSerializer
   //새로운 에러가 발생함.
    @GetMapping("/api/v1/simple-orders")
    public List<Order> ordersV1(){
        List<Order> all = orderRepository.findAllByCriteria(new OrderSearch());

        return all;
    }


    //V2 -> DTO로 변환
    @GetMapping("/api/v2/simple-orders")
    public List<SimpleOrderDto> ordersV2(){
        List<Order> orders = orderRepository.findAllByCriteria(new OrderSearch());
        List<SimpleOrderDto> result = new ArrayList<>();
        for (Order o : orders) {
            SimpleOrderDto simpleOrderDto = new SimpleOrderDto(o);
            result.add(simpleOrderDto);
        }
        return result;
    }

    @Data
    static class SimpleOrderDto {
        private Long orderId;
        private String name;
        private LocalDateTime orderDate;
        private OrderStatus orderStatus;
        private Address address;

        public SimpleOrderDto(Order order){
            orderId = order.getId();
            name = order.getMember().getName(); //LAZY 초기화
            orderDate = order.getOrderDate();
            orderStatus = getOrderStatus();
            address = order.getDelivery().getAddress(); //LAZY 초기화
        }
    }
}
