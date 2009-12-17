package jsyntaxpane.lexers;

import jsyntaxpane.DefaultLexer;
import jsyntaxpane.Token;
import jsyntaxpane.TokenType;

%%

%public
%class HttpdConfLexer
%extends DefaultLexer
%final
%unicode
%char
%type Token
%caseless


%{
    /**
     * Default constructor is needed as we will always call the yyreset
     */
    public HttpdConfLexer() {
        super();
    }

    /**
     * Helper method to create and return a new Token from of TokenType
     */
    private Token token(TokenType type) {
        return new Token(type, yychar, yylength());
    }

%}

/* main character classes */
LineTerminator = \r|\n|\r\n
InputCharacter = [^\r\n]

WhiteSpace = {LineTerminator} | [ \t\f]

/* comments */
Comment = {EndOfLineComment}

EndOfLineComment = "#" {InputCharacter}* {LineTerminator}?

/* identifiers */
Identifier = [:jletter:][:jletterdigit:]*

/* integer literals */
DecIntegerLiteral = 0 | [1-9][0-9]*
    
/* floating point literals */        
FloatLiteral  = ({FLit1}|{FLit2}|{FLit3}) {Exponent}? [fF]

FLit1    = [0-9]+ \. [0-9]* 
FLit2    = \. [0-9]+ 
FLit3    = [0-9]+ 
Exponent = [eE] [+-]? [0-9]+

/* string and character literals */
StringCharacter = [^\r\n\"\\]
SingleCharacter = [^\r\n\'\\]

Reserved = 
   "ServerRoot"                 |
   "ScoreBoardFile"                 |
   "PidFile"               |
   "Timeout"             |
   "KeepAlive"                 |
   "On"                  |
   "Off"                 |
   "MaxKeepAliveRequests"              |
   "KeepAliveTimeout"             |
   "ThreadsPerChild"              |
   "MaxRequestsPerChild"              |
   "LoadModule"                |
   "ExtendedStatus"                |
   "ServerAdmin"                  |
   "ServerName"                |
   "UseCanonicalName"             |
   "DocumentRoot"                |
   "Options"              |
   "FollowSymLinks"                |
   "AllowOverride"           |
   "None"               |
   "Indexes"             |
   "AllowOverride"              |
   "allow"           |
   "deny"          |
   "from"            |
   "all"             |
   "UserDir"              |
   "SymLinksIfOwnerMatch"               |
   "IncludesNoExec"              |
   "FileInfo"            |
   "AuthConfig"           |
   "DirectoryIndex"                 |
   "AccessFileName"             |
   "TypesConfig"             |
   "DefaultType"             |
   "MIMEMagicFile"             |
   "HostnameLookups"              |
   "EnableMMAP"                |
   "ErrorLog"            |
   "LogLevel"       |
   "debug"            |
   "info"         |
   "notice"                 |
   "warn"              |
   "error" |
   "crit"                |
   "alert" |
   "emerg" |
   "LogFormat"                |
   "combined"                |
   "common"                |
   "referer"              |
   "agent"            |
   "CustomLog"             |
   "ServerTokens"              |
   "Full"                |
   "OS"             |
   "Minor"               |
   "Minimal"               |
   "Major"               |
   "Prod"              |
   "ServerSignature"              |
   "EMail"                 |
   "Alias"               |
   "MultiViews"             |
   "AliasMatch"                |
   "SetHandler"            |
   "SetEnvIf"               |
   "RedirectMatch"               |
   "ScriptAlias"              |
   "Redirect"                  |
   "permanent"              |
   "IndexOptions"                  |
   "FancyIndexing"               |
   "VersionSort"              |
   "AddIconByEncoding"               |
   "AddIconByType"               |
   "AddIcon"         |
   "DefaultIcon"              |
   "AddDescription"                 |
   "ReadmeName"             |
   "HeaderName"            |
   "IndexIgnore"                |
   "DefaultLanguage"                  |
   "LanguagePriority"             |
   "ForceLanguagePriority"                |
   "Prefer"                 |
   "Fallback"                |
   "AddCharset"                |
   "AddType"             |
   "AddEncoding"               |
   "AddHandler"                |
   "AddOutputFilter"                |
   "Includes"               |
   "ErrorDocument"               |
   "BrowserMatch"                |
   "nokeepalive"                |
   "Include"                |
   "NameVirtualHost" |
   "IfModule" |
   "Listen" |
   "Directory" |
   "FilesMatch" |
   "Files" |
   "AddLanguage" |
   "Order" |
   "Location" |
   "EnableSendfile" |
   "Limit" |
   "LimitExcept" |
   "VirtualHost"


%%

<YYINITIAL> {

  /* keywords */
  {Reserved}                     { return token(TokenType.KEYWORD); }
  
  /* operators */

  "("                            |
  ")"                            |
  "{"                            | 
  "}"                            | 
  "["                            | 
  "]"                            | 
  ";"                            | 
  ","                            | 
  "."                            | 
  "@"                            | 
  "="                            | 
  ">"                            | 
  "<"                            |
  "!"                            | 
  "~"                            | 
  "?"                            | 
  ":"                            { return token(TokenType.OPERATOR); } 

  /* string literal */
  \"{StringCharacter}+\"         | 

  \'{SingleCharacter}+\          { return token(TokenType.STRING); } 

  /* numeric literals */

  {DecIntegerLiteral}            |
 
  {FloatLiteral}                 { return token(TokenType.NUMBER); }
  
  /* comments */
  {Comment}                      { return token(TokenType.COMMENT); }

  /* whitespace */
  {WhiteSpace}+                  { /* skip */ }

  /* identifiers */ 
  {Identifier}                   { return token(TokenType.IDENTIFIER); }

}

/* error fallback */
.|\n                             {  }
<<EOF>>                          { return null; }

