package win.betty35.www.myPRL.bean;

import java.util.Date;

public class Comment 
{
	private String text,source;
	private Long commentID,PID,OriginalID;
	private boolean additional;
	private Date updatedDate;
	
	public String getText() {
		return text;
	}
	public void setText(String text) {
		this.text = text;
	}
	public String getSource() {
		return source;
	}
	public void setSource(String source) {
		this.source = source;
	}
	public Long getCommentID() {
		return commentID;
	}
	public void setCommentID(Long commentID) {
		this.commentID = commentID;
	}
	public Long getPID() {
		return PID;
	}
	public void setPID(Long pID) {
		PID = pID;
	}
	public Long getOriginalID() {
		return OriginalID;
	}
	public void setOriginalID(Long originalID) {
		OriginalID = originalID;
	}
	public boolean isAdditional() {
		return additional;
	}
	public void setAdditional(boolean additional) {
		this.additional = additional;
	}
	public Date getUpdatedDate() {
		return updatedDate;
	}
	public void setUpdatedDate(Date updatedDate) {
		this.updatedDate = updatedDate;
	}
}
