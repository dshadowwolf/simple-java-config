package com.keildraco.config.factory;

import com.keildraco.config.types.ParserInternalTypeBase;

public interface IParserType {
	public ParserInternalTypeBase get(ParserInternalTypeBase parent, String name, String value);
}
