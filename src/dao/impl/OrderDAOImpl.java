package dao.impl;

import dao.OrderDAO;
import db.DBConnection;
import entity.Customer;
import entity.Item;
import entity.Order;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class OrderDAOImpl implements OrderDAO {

    public String getLastOrderId(){
        try {
            Connection connection = DBConnection.getInstance().getConnection();
            Statement stm = connection.createStatement();
            ResultSet rst = stm.executeQuery("SELECT * FROM `order` ORDER BY orderId DESC  LIMIT 1");
            if (rst.next()){
                return rst.getString(1);
            }else{
                return null;
            }
        } catch (SQLException throwables) {
            throwables.printStackTrace();
            return null;
        }
    }

    public List<Object> findAll(){
        Connection connection = DBConnection.getInstance().getConnection();
        try {
            Statement stm = connection.createStatement();
            ResultSet rst = stm.executeQuery("SELECT * FROM `order`");
            ArrayList<Object> orders = new ArrayList<>();
            while (rst.next()){
                orders.add( new Order(rst.getString(1), rst.getDate(2),
                        rst.getString(3)));
            }
            return orders;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public Object find(Object key){
        Connection connection = DBConnection.getInstance().getConnection();
        try {
            PreparedStatement pstm = connection.prepareStatement("SELECT * FROM `order` WHERE orderId =?");
            pstm.setObject(1,key);
            ResultSet rst = pstm.executeQuery();
            if (rst.next()){
                return new Order(rst.getString(1), rst.getDate(2),
                        rst.getString(3));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public boolean save(Object entity){
        Connection connection = DBConnection.getInstance().getConnection();
        try {
            PreparedStatement pstm = connection.prepareStatement("INSERT INTO `order` VALUES (?,?,?)");
            Order order = (Order) entity;
            pstm.setObject(1,order.getOrderId());
            pstm.setObject(2,order.getOrderDate());
            pstm.setObject(3,order.getCustomerId());
            return pstm.executeUpdate()>0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean update(Object entity){
        Connection connection = DBConnection.getInstance().getConnection();
        try {
            PreparedStatement pstm = connection.prepareStatement
                    ("UPDATE `order` SET orderDate=?,customerId=? WHERE orderId =?)");
            Order order = (Order) entity;
            pstm.setObject(3,order.getOrderId());
            pstm.setObject(1,order.getOrderDate());
            pstm.setObject(2,order.getCustomerId());
            return pstm.executeUpdate()>0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean delete(Object key){
        Connection connection = DBConnection.getInstance().getConnection();
        try {
            PreparedStatement pstm = connection.prepareStatement("DELETE FROM `order` WHERE orderId =?");
            pstm.setObject(1,key);
            return pstm.executeUpdate()>0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
}