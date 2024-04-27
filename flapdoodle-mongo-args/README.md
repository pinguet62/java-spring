# Embedded Mongodb & Custom args

Custom annotation `@EmbeddedMongodbArg` used to define additional arguments to use during Mongodb startup.

Example: `@EmbeddedMongodbArg(key = "--notablescan")` will enable `--notablescan` CLI option (then queries will require index usage).
