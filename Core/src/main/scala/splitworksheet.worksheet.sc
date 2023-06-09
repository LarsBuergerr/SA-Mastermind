
val str = "rgby 4"

var splitted_str = str.split(" ")
var action = ""
var num = 0

if splitted_str.length > 1 then
  action = splitted_str(0)
  num = splitted_str(1).toInt
else
  action = splitted_str(0)
  num = 0

print(action)
print(num)