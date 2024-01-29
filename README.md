# 24
had to learn more java after ftc  
inspiration from another project  
reference: https://github.com/auntyellow/24  
  
solves for all non negative integers  
4 number format, + - * / only  
  
generates dissimiliar unique solutions with filters only  
- filters duplicate solutions for duplicate numbers ((x,x,y,y), etc...)
- filters useless 0's (+0,0+,-0,0-)
  - filters dragged 0's as well (0\*a, 0\*(a+b), 0\*(a+(b+c)))
- filters useless 1's (\*1,1\*,/1)
  - in the case of 1/x , cases are handled individually
- filters (2\*2) as = 2+2
- filters (4/2) as = 4-2
- filters all of the above but for made numbers
  - filters useless made 0's (x-x, etc..)
  - filters useless made 1's (a-b when b = a-1, ...)
  - filters useless made 2's and 4's for 2\*2 and 4/2
- filters clones
  - doubles ((2\*x)-x) as = (x+x)/2
  - triples (((3\*x)-x)-x) as = (x+(x+x))/3
- filters cases where x+y=z or x\*y=z
  - x+y=z makes +((x+y)-z) and \*((x+y)/z) the same
  - x\*y=z makes \*((x\*y)/z) and +((x\*y)-z) the same

evaluates difficulty score for each solution and task with these metrics,  
tuned using data from [www.4nums.com](https://www.4nums.com/game/difficulties/)  
- number of solutions for task
- number of useless 0's
- number of dragged 0's
  - how many numbers dragged
- number of useless made 0's
- number of useless 1's
- number of useless made 1's
- largest number solution approaches
- does solution have fractions
- the arrangement of the operators ((o+(o/o)-o, (o+o)\*(o-o), etc...)
- the pattern of duplicate numbers ((a,b,c,d), (a,a,b,b), (a,a,b,c), etc...)
- sum of the four numbers
- solutions in form (a\*b)+-(a\*c) (similiar to a\*(b+-c))

although the purpose of the project was to study the relations between the different rules and the difficulty of the game, a simple program was made as a proof of concept for making use of this algorithm  
run Interactive.java in console for a very basic demonstration, or just run Executor.java which will run Interactive.java in windows terminal  
itll also show fractional solutions, large solutions, and partial solutions (result has difference <= 1 to target) as well as estimated score  
do be aware that there are 4 stacked for loops so large input ranges will slow it down to a halt
