/*
 * Copyright (C) 2007 by
 * 
 * 	Xuan-Hieu Phan
 *	hieuxuan@ecei.tohoku.ac.jp or pxhieu@gmail.com
 * 	Graduate School of Information Sciences
 * 	Tohoku University
 * 
 *  Cam-Tu Nguyen
 *  ncamtu@gmail.com
 *  College of Technology
 *  Vietnam National University, Hanoi
 *
 * JGibbsLDA is a free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published
 * by the Free Software Foundation; either version 2 of the License,
 * or (at your option) any later version.
 *
 * JGibbsLDA is distributed in the hope that it will be useful, but
 * WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with JGibbsLDA; if not, write to the Free Software Foundation,
 * Inc., 59 Temple Place, Suite 330, Boston, MA 02111-1307 USA.
 */


/* modified by
 * Betty B Zhao (betty352008@yeah.net)
 * 
 * word_id_map is saved in mysql database now
 * */

package core.algorithm.lda;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import win.betty35.www.myPRL.Pre.dbUtils.DB_LDA;
import win.betty35.www.myPRL.Pre.dbUtils.common.Configure;

public class Dictionary implements Serializable{
	
	private static final long serialVersionUID = -7709563464351645654L;
	
	public Map<String,Integer> word2id;
	public Map<Integer, String> id2word;
	
	//--------------------------------------------------
	// constructors
	//--------------------------------------------------
	
	public Dictionary(){
		word2id = new HashMap<String, Integer>();
		id2word = new HashMap<Integer, String>();
		this.readWordMap();
	}
	
	//---------------------------------------------------
	// get/set methods
	//---------------------------------------------------
	
	public String getWord(int id){
		return id2word.get(id);
	}
	
	public Integer getID (String word){
		return word2id.get(word);
	}
	
	//----------------------------------------------------
	// checking methods
	//----------------------------------------------------
	/**
	 * check if this dictionary contains a specified word
	 */
	public boolean contains(String word){
		return word2id.containsKey(word);
	}
	
	public boolean contains(int id){
		return id2word.containsKey(id);
	}
	//---------------------------------------------------
	// manupulating methods
	//---------------------------------------------------
	/**
	 * add a word into this dictionary
	 * return the corresponding id
	 */
	public int addWord(String word){
		if (!contains(word)){
			int id = word2id.size();
			
			word2id.put(word, id);
			id2word.put(id,word);
			
			return id;
		}
		else return getID(word);		
	}
	
	//---------------------------------------------------
	// I/O methods
	//---------------------------------------------------
	/**
	 * read dictionary from file
	 */
	public boolean readWordMap(){		
		Configure c=new Configure();
		DB_LDA l=new DB_LDA(c);
		boolean status=l.initDict(word2id, id2word);
		l.close();
		return status;
	}
	
	public boolean writeWordMap(){
			Configure c=new Configure();
			DB_LDA l=new DB_LDA(c);
			//write word to id
			Iterator<String> it = word2id.keySet().iterator();
			while (it.hasNext()){
				String key = it.next();
				Integer value = word2id.get(key);
				l.insertTerm(value,key);
			}
			l.close();
			return true;
	}
}
