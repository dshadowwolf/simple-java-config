package com.keildraco.config.data;

import com.keildraco.config.types.ParserInternalTypeBase;

import static com.keildraco.config.types.ParserInternalTypeBase.ItemType;

import com.keildraco.config.Config;
import com.keildraco.config.types.SectionType;

import com.keildraco.config.types.IdentifierType;
import com.keildraco.config.types.ListType;
import com.keildraco.config.types.OperationType;

public class ItemMatcher {
	private final ParserInternalTypeBase thisItem;
	
	public static final class AlwaysFalse extends ItemMatcher {
		@Override
		public boolean matches(String name) {
			return false;
		}
	}
	
	private ItemMatcher() {
		throw new IllegalAccessError("Cannot instantiate class this way!");
	}
	
	public ItemMatcher(ParserInternalTypeBase item) {
		this.thisItem = item;
	}

	public boolean matches(String name) {
		boolean tn = name.contains(".");
		String bn = tn?name.substring(0, name.indexOf('.')):name;
		String xn = tn?name.substring(name.indexOf('.')+1):"";
		
		return this.doMatch(this.thisItem.getType(),bn,xn);
	}

	private boolean doMatch(ItemType type, String bn, String xn) {
		switch(type) {
		case IDENTIFIER:
			if(xn.length() > 0) return this.identMatches((IdentifierType)this.thisItem,xn)&&this.thisItem.getName().equalsIgnoreCase(bn);
			else return this.identMatches((IdentifierType)this.thisItem,bn);
		case LIST:
			if(this.thisItem.has(bn) && xn.length() > 0) return (new ItemMatcher(this.thisItem.get(bn))).matches(xn);
			else return this.listMatchesAny(bn);
		case OPERATION:
			return this.operatorMatches(xn.length()>0?xn:bn);
		case SECTION:
			if(xn.length() > 0) return (new ItemMatcher(this.thisItem.get(bn))).matches(xn);
			return this.sectionMatches(bn);
		default:
			return false;
		}
	}

	private boolean sectionMatches(SectionType sec, String name) {
		return sec.has(name);
	}
	
	private boolean sectionMatches(String name) {
		return this.sectionMatches((SectionType)this.thisItem, name);
	}

	private boolean matchOperator(OperationType op, String itemName, String valueName) {
		String matchName = itemName;
		if(op.getName().equalsIgnoreCase(itemName) && valueName.length() > 0) {
			matchName = valueName;
		}
		
		if(op.getOperator() == '!') return !op.getValue().equalsIgnoreCase(matchName);
		else if(op.getOperator() == '~') return op.getValue().equalsIgnoreCase(matchName);
		return true;
	}
	
	private boolean operatorMatches(OperationType op, String name) {
		if(name.indexOf('.') != -1) {
			String in = name.substring(0, name.indexOf('.'));
			String vn = name.substring(name.indexOf('.')+1);
			return this.matchOperator(op, in, vn);
		} else {
			return this.matchOperator(op, name, "");
		}
	}
	
	private boolean operatorMatches(String name) {
		return this.operatorMatches((OperationType)this.thisItem, name);
	}

	private boolean identMatches(IdentifierType ident, String name) {
		Config.LOGGER.fatal(">> %s -- %s -- %s", ident.getName(), ident.getValue(), name);
		return (ident.getName().equalsIgnoreCase(name) || ident.getValue().equalsIgnoreCase(name));
	}
	
	private boolean listMatchesAny(ListType theList, String name) {
		return theList.has(name);
	}
	
	private boolean listMatchesAny(String name) {
		return this.listMatchesAny((ListType)this.thisItem,name);
	}
}
