# stock_backup
stock项目备份


登录接口
GET http://localhost:8099/api/loginByMini
个股搜索接口
GET http://localhost:8099/api/search_stock
Content-Type: application/json
{
 "code":"000",
  "name":"jin",
  "pageSize":10,
  "pageNum" :1
}
获取大盘指数接口

GET http://localhost:8099/api/composite_index

return List<日线行情>[上证，深证，创业板]
相似度分析接口
GET http://localhost:8099/api/stock_analysis
Content-Type: application/json

{
  "code": "000159",
  "range": 30,
"preRange":5,
"model":"KL"

}
 
