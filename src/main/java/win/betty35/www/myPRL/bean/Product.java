package win.betty35.www.myPRL.bean;

import java.util.ArrayList;

import win.betty35.www.myPRL.MultiScore.Score;

public class Product 
{
	private Long productID;
	private Long id;
	private String source;
	private String title;
	private String filename;
	private double price;
	private float description;
	private float attitude;
	private float delivery;
	private String shop;
	private String place;
	private Long sales;
	private Long shopId;
	private String page;
	private ArrayList<Comment> commentList;
	public Score[] multiScore;
	
	public Product()
	{
		multiScore=null;
	}
	
	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
	}
	public String getTitle() {
		return title;
	}
	public void setTitle(String title) {
		this.title = title;
	}
	public String getFilename() {
		return filename;
	}
	public void setFilename(String filename) {
		this.filename = filename;
	}
	public double getPrice() {
		return price;
	}
	public void setPrice(double price) {
		this.price = price;
	}
	public float getDescription() {
		return description;
	}
	public void setDescription(float description) {
		this.description = description;
	}
	public float getAttitude() {
		return attitude;
	}
	public void setAttitude(float attitude) {
		this.attitude = attitude;
	}
	public float getDelivery() {
		return delivery;
	}
	public void setDelivery(float delivery) {
		this.delivery = delivery;
	}
	public String getShop() {
		return shop;
	}
	public void setShop(String shop) {
		this.shop = shop;
	}
	public String getPlace() {
		return place;
	}
	public void setPlace(String place) {
		this.place = place;
	}
	public Long getSales() {
		return sales;
	}
	public void setSales(Long sells) {
		this.sales = sells;
	}
	public String getSource() {
		return source;
	}
	public void setSource(String source) {
		this.source = source;
	}
	public Long getShopId() {
		return shopId;
	}
	public void setShopId(Long shopId) {
		this.shopId = shopId;
	}
	public String getPage() {
		return page;
	}
	public void setPage(String page) {
		this.page = page;
	}
	public ArrayList<Comment> getCommentList() {
		return commentList;
	}
	public void setCommentList(ArrayList<Comment> commentList) {
		this.commentList = commentList;
	}

	public Long getProductID() {
		return productID;
	}

	public void setProductID(Long productID) {
		this.productID = productID;
	}
	
	
}
