# Test

## Utils

### Java SAX (Simple API for XML) utilities

* Converter: from `String` to `Source`
* XSD validation: `LSResourceResolver` based on local *relative path*

### Hexavigesimal

Wrapper for Hexavigesimal representation and conversions.

#### Description

Hexavigesimal is the base-26.

Numbers are represented using only [Basic Latin alphabet](http://en.wikipedia.org/wiki/ISO_basic_Latin_alphabet).

Decimal | Hexavigesimal
:-----: | :-----------:
0     | `A`
1     | `B`
2     | `C`
...   | ...
24    | `Y`
25    | `Z`
26    | `AA`
27    | `AB`
...   | ...
700   | `ZZ`
701   | `AAA`
702   | `AAB`
...   | ...

#### Warning !

In other base as *octal*, *decimal*, *hexadecimal*, ..., the zero at the beginning of the number is unnecessary:
<br />
`0` = `00` = `000` = ...
<br />
`01` = `01` = `001` = ...
<br />
`02` = `02` = `002` = ...
<br />
...

But in *hexavigesimal* :

Hexavigesimal |                              Conversion                               | Decimal
:-----------: | --------------------------------------------------------------------: | :-----:
`A`           |                                                 **0**\*26<sup>0</sup> | 0
`AA`          |                         **1**\*26<sup>1</sup> + **0**\*26<sup>0</sup> | 26
`AAA`         | **1**\*26<sup>2</sup> + **1**\*26<sup>1</sup> + **0**\*26<sup>0</sup> | 702

#### Excel

Into calc documents as Excel, there is a offset of **1** into column index: it beginning to `A1` but not `A0`.
