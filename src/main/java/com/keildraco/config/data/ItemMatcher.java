package com.keildraco.config.data;

import com.keildraco.config.types.ParserInternalTypeBase;
import com.keildraco.config.types.SectionType;

import java.util.Map.Entry;
import java.util.stream.Collectors;

import com.keildraco.config.types.IdentifierType;
import com.keildraco.config.types.ListType;
import com.keildraco.config.types.OperationType;

public class ItemMatcher {
	private final ParserInternalTypeBase thisItem;
	
	public static final class AlwaysFalse extends ItemMatcher {
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
		switch(this.thisItem.getType()) {
			case IDENTIFIER:
				return this.identMatches(name);
			case LIST:
				return this.listMatchesAny(name);
			case OPERATION:
				return this.operatorMatches(name);
			case SECTION:
				return this.sectionMatches(name);
			default:
				return false;
		}
	}

	private boolean sectionMatches(SectionType sec, String name) {
		boolean rv = false;
		for( Entry<String, ParserInternalTypeBase> ent : sec.getChildren().entrySet()) {
			if( rv == true ) break;
			ItemMatcher p = new ItemMatcher(ent.getValue());
			rv = p.matches(name);
		}
		return rv;
	}
	
	private boolean sectionMatches(String name) {
		return this.sectionMatches((SectionType)this.thisItem, name);
	}

	private boolean operatorMatches(OperationType op, String name) {
		if( op.getName().equalsIgnoreCase(name) || op.getValue().equalsIgnoreCase(name) ) {
			int oper = op.getOperator();
			if(oper == '!') return false;
			return true;
		}
		return false;
	}
	private boolean operatorMatches(String name) {
		return this.operatorMatches((OperationType)this.thisItem, name);
	}

	private boolean identMatches(IdentifierType ident, String name) {
		if( ident.getName().equalsIgnoreCase(name) || ident.getValue().equalsIgnoreCase(name) ) return true;
		return false;		
	}
	private boolean identMatches(String name) {
		return this.identMatches((IdentifierType)this.thisItem, name);
	}

	private boolean matches(ParserInternalTypeBase pitb, String name) {
		switch(pitb.getType()) {
		case IDENTIFIER:
			return this.identMatches((IdentifierType)pitb, name);
		case LIST:
			return this.listMatchesAny((ListType)pitb, name);
		case OPERATION:
			return this.operatorMatches((OperationType)pitb, name);
		case SECTION:
			return this.sectionMatches((SectionType)pitb, name);
		default:
			return false;
		}
	}
		
	private boolean listMatchesAny(ListType theList, String name) {
		return theList.toList().stream()
		.map( p -> this.matches(p, name))
		.collect(Collectors.toList())
		.contains(Boolean.TRUE);
	}
	
	private boolean listMatchesAny(String name) {
		return this.listMatchesAny((ListType)this.thisItem,name);
	}
}
