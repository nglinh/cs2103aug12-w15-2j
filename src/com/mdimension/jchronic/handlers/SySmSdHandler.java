package com.mdimension.jchronic.handlers;

import java.util.LinkedList;
import java.util.List;

import com.mdimension.jchronic.Options;
import com.mdimension.jchronic.utils.Span;
import com.mdimension.jchronic.utils.Token;

public class SySmSdHandler extends SmSdSyHandler {

  @Override
  public Span handle(List<Token> tokens, Options options) {
    List<Token> newTokens = new LinkedList<Token>();
    newTokens.add(tokens.get(1));
    newTokens.add(tokens.get(2));
    newTokens.add(tokens.get(0));
    newTokens.addAll(tokens.subList(3, tokens.size()));
    return super.handle(newTokens, options);
  }
}
