package book;

public class BookList {
	public String id;
	public String bookName,writer,price;
	
	@Override
	public String toString() {
		return getId()+"  "+getBookName()+"  "+getWriter()+"  "+getPrice();
	}
	
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public String getBookName() {
		return bookName;
	}
	public void setBookName(String bookName) {
		this.bookName = bookName;
	}
	public String getWriter() {
		return writer;
	}
	public void setWriter(String writer) {
		this.writer = writer;
	}
	public String getPrice() {
		return price;
	}
	public void setPrice(String price) {
		this.price = price;
	}
}
