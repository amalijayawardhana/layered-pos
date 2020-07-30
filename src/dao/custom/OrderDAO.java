package dao.custom;

import dao.SuperDAO;
import entity.Order;

public interface OrderDAO extends SuperDAO<Order,String> {
    public String getLastOrderId();
/*    public List<Order> findAllOrders();
    public Order findOrder(String orderId);
    public boolean saveOrder(Order order);
    public boolean updateOrder(Order order);
    public boolean deleteOrder(String orderId);*/

    }