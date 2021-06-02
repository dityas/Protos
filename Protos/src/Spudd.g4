grammar Spudd;

@header {
	package thinclab.spuddx_parser;
}

/*
	Parser
*/

line: chars EOF;

chars: TEXT;

TEXT: [a-Z];