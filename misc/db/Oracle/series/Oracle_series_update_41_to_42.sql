--update numeric columns to double
ALTER TABLE NUMERICVALUE MODIFY VALUE DOUBLE PRECISION;
ALTER TABLE SERIES MODIFY FIRSTNUMERICVALUE DOUBLE PRECISION;
ALTER TABLE SERIES MODIFY LASTNUMERICVALUE DOUBLE PRECISION;