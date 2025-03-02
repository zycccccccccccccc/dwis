
-- 批量更新每个表的当前ID种子值
IF (OBJECT_ID('sp_reseed', 'P') IS NOT NULL)
DROP PROC sp_reseed
GO

CREATE PROC sp_reseed
AS
BEGIN
  DECLARE @tableName VARCHAR(128) -- 表名
  DECLARE @idName VARCHAR(128) -- 主键字段名
  DECLARE @sqlstr NVARCHAR(1024) -- 执行sql临时字符串
  DECLARE @currentId VARCHAR(128) -- ID当前值
  DECLARE tableNameCursor CURSOR FOR (
    SELECT c.name, o.name
    FROM syscolumns c JOIN sysobjects o ON c.id = o.id
    WHERE c.xtype = 56 AND c.colid = 1 AND o.xtype = 'U'
  )

  OPEN tableNameCursor;
  FETCH NEXT FROM tableNameCursor INTO @idName, @tableName;
  
  WHILE @@FETCH_STATUS = 0
  BEGIN 
    -- 取每个表ID的最大值作为当前ID种子值
    FETCH NEXT FROM tableNameCursor INTO @idName, @tableName
    SET @sqlstr = 'SELECT @a = MAX(' + @idName + ') + 1 FROM ' + @tableName
    EXEC sp_executesql @sqlstr, N'@a VARCHAR(128) output', @currentId OUTPUT
    PRINT @tableName + ' ' + @currentId
    
    -- 批量更新每个表的当前ID种子值
    SET @sqlstr = 'DBCC CHECKIDENT (' + @tableName + ', reseed, ' + @currentId + ')'
    EXEC sp_executesql @sqlstr
  END
  
  CLOSE tableNameCursor
  DEALLOCATE tableNameCursor
END
GO


EXEC sp_reseed
GO

