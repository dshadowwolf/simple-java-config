package com.keildraco.config.factory;

import javax.annotation.Nullable;

import com.keildraco.config.types.ParserInternalTypeBase;

public interface IParserType {
	ParserInternalTypeBase get(@Nullable ParserInternalTypeBase parent, String name, String value);
}
