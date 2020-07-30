package business;


import dao.DAOFactory;
import dao.custom.CustomerDAO;
import dao.custom.ItemDAO;
import dao.custom.OrderDAO;
import dao.custom.impl.CustomerDAOImpl;

import dao.custom.impl.ItemDAOImpl;

import dao.custom.impl.OrderDAOImpl;

import dao.custom.impl.OrderDetailDAOImpl;
import db.DBConnection;
import entity.Customer;
import entity.Item;
import entity.Order;
import entity.OrderDetail;
import util.CustomerTM;
import util.ItemTM;
import util.OrderDetailTM;
import util.OrderTM;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.Date;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class BusinessLogic {
    public static String getNewCustomerId(){
        CustomerDAOImpl customerDAO = new CustomerDAOImpl();
        String lastCustomerId = customerDAO.getLastCustomerId();
        if (lastCustomerId == null){
            return "C001";
        }else{
            int maxId=  Integer.parseInt(lastCustomerId.replace("C",""));
            maxId = maxId + 1;
            String id = "";
            if (maxId < 10) {
                id = "C00" + maxId;
            } else if (maxId < 100) {
                id = "C0" + maxId;
            } else {
                id = "C" + maxId;
            }
            return id;
        }
    }

    public static List<CustomerTM> getAllCustomers(){
        CustomerDAO customerDAO = DAOFactory.getInstance().getCustomerDAO();
        List<Customer> allCustomers = customerDAO.findAll();
        ArrayList<CustomerTM> customers = new ArrayList<>();
        for (Object c : allCustomers) {
            Customer customer = (Customer) c;
            customers.add(new CustomerTM(customer.getId(),customer.getName(),customer.getAddress()));
        }
        return customers;
    }

    public static boolean saveCustomer(String id, String name, String address){
        CustomerDAO customerDAO = DAOFactory.getInstance().getCustomerDAO();
        return customerDAO.save(new Customer(id,name,address));
    }

    public static boolean deleteCustomer(String customerId){
        CustomerDAO customerDAO = DAOFactory.getInstance().getCustomerDAO();
        return customerDAO.delete(customerId);
    }


    public static boolean updateCustomer(String name, String address, String customerId){
        CustomerDAO customerDAO = DAOFactory.getInstance().getCustomerDAO();
        return customerDAO.update(new Customer(customerId, name, address));
    }

    public static List<ItemTM> getAllItems(){
        ItemDAO itemDAO = DAOFactory.getInstance().getItemDAO();
        List<Item> allItems = itemDAO.findAll();
        ArrayList<ItemTM> items = new ArrayList<>();
        for (Object i : allItems) {
            Item item = (Item) i;
            items.add(new ItemTM(item.getItemcode(),item.getDescription(),item.getqtyOnHand(),item.getUnitprice().doubleValue()));
        }
        return items;
    }

    public static String getNewitemCode(){
        ItemDAO itemDAO = DAOFactory.getInstance().getItemDAO();
        String lastitemCode = itemDAO.getLastitemCode();
        if (lastitemCode == null){
            return "I001";
        }else{
            int maxId=  Integer.parseInt(lastitemCode.replace("I",""));
            maxId = maxId + 1;
            String id = "";
            if (maxId < 10) {
                id = "I00" + maxId;
            } else if (maxId < 100) {
                id = "I0" + maxId;
            } else {
                id = "I" + maxId;
            }
            return id;
        }
    }

    public static boolean saveItem(String code, String description, int qtyOnHand, double unitPrice){
        ItemDAO itemDAO = DAOFactory.getInstance().getItemDAO();
        return itemDAO.save(new Item(code, description, BigDecimal.valueOf(unitPrice), qtyOnHand));
    }

    public static boolean deleteItem(String itemCode){
        ItemDAO itemDAO = DAOFactory.getInstance().getItemDAO();
        return itemDAO.delete(itemCode);
    }

    public static boolean updateItem(String description, int qtyOnHand, double unitPrice, String itemCode){
        ItemDAO itemDAO = DAOFactory.getInstance().getItemDAO();
        return itemDAO.update(new Item(itemCode, description, BigDecimal.valueOf(unitPrice),qtyOnHand));
    }

    public static String getNewOrderId(){
        OrderDAO orderDAO = DAOFactory.getInstance().getOrderDAO();
        String lastOrderId = orderDAO.getLastOrderId();
        if (lastOrderId == null){
            return "D001";
        }else{
            int maxId=  Integer.parseInt(lastOrderId.replace("D",""));
            maxId = maxId + 1;
            String id = "";
            if (maxId < 10) {
                id = "D00" + maxId;
            } else if (maxId < 100) {
                id = "D0" + maxId;
            } else {
                id = "D" + maxId;
            }
            return id;
        }
    }
    public static boolean placeOrders(OrderTM order, List<OrderDetailTM> orderDetails) {
        Connection connection = DBConnection.getInstance().getConnection();
        OrderDAO orderDAO = DAOFactory.getInstance().getOrderDAO();
        OrderDetailDAOImpl orderDetailDAO = new OrderDetailDAOImpl();
        try {
            connection.setAutoCommit(false);

            boolean result = orderDAO.save(new Order(order.getOrderId(), Date.valueOf(order.getOrderDate()), order.getCustomerId()));
            if (!result) {
                connection.rollback();
                return false;
            }
            for (OrderDetailTM orderDetail : orderDetails) {
                result = orderDetailDAO.save(new OrderDetail(order.getOrderId(), orderDetail.getCode(), orderDetail.getQty(), BigDecimal.valueOf(orderDetail.getUnitPrice())));

                if (!result) {
                    connection.rollback();
                    return false;
                }
                ItemDAO itemDAO = DAOFactory.getInstance().getItemDAO();
                Item item = itemDAO.find(orderDetail.getCode());
                item.setQtyOnHand(item.getqtyOnHand()-orderDetail.getQty());
                System.out.println(item.getqtyOnHand()-orderDetail.getQty());
                result = itemDAO.update(item);
                System.out.println(item);

                if (!result) {
                    connection.rollback();
                    return false;
                }

                connection.commit();
                return true;
            }
        } catch (SQLException throwables) {
            throwables.printStackTrace();
            try {
                connection.rollback();
            } catch (SQLException e) {
                e.printStackTrace();
            }
            return false;
        } finally {
            try {
                connection.setAutoCommit(true);
            } catch (SQLException throwables) {
                throwables.printStackTrace();
            }
        }
        return false;
    }
}
