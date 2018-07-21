package com.keildraco.config.factory;

import com.keildraco.config.states.IStateParser;
import com.keildraco.config.types.ParserInternalTypeBase;

public interface IParserState {
	public IStateParser get();
}
