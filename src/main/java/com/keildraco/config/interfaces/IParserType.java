package com.keildraco.config.interfaces;

import javax.annotation.Nullable;

public interface IParserType {

	ParserInternalTypeBase get(@Nullable ParserInternalTypeBase parent, String name, String value);
}
