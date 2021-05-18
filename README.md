
# Checkers-Ai
- **By** Benamara Abdelkader Chihab Eddine & Djelid Aymen

**CheckersAi** is a 2-players checkers Game where we can choose to play against a friend or an **Ai** which made us to implement **MiniMax** algorithms for adverserial 2-players games and we also implemented the **$\alpha-\beta$ Pruning** version for this algorithm.
The implementation is in `Java` with a simple GUI to make user's experience much easier.

# Get Started

You need to have `Java` installed ( any **JDK** is accepted for this version ).
To get started with you should compile classes using this command ( Let's say we are in the project's root ) :
> **javac** Main.java

Or
> **javac** *.java

And Then we need to run this compiled classes by :
> **java** Main

# After running ! [Screentshots]

Just with making the run command you will have this window with some options :
![1](https://user-images.githubusercontent.com/38104305/104843983-be595e80-58cd-11eb-9dbc-0e7118fd30f4.JPG)

You can choose the level of the difficulty before starting the experience
> **PS :**  Changing the difficulty level is actually changing the maximum depth to search in the game tree
>  **For example :** Hard level is about **12** floors of the game Tree ( $\alpha-\beta$ pruning ) & **9** in the basic version ( so we could see the performance given by this pruning ).

>  **Note** If you have a better performance you can increase this depth easily by going into the corresponding Player class and just before giving a call to minimax() or alpha_beta() methods.

Here after one move for each Player : ( **Human** vs **MinMax** )
![2](https://user-images.githubusercontent.com/38104305/104843982-bdc0c800-58cd-11eb-919a-d905f03f843c.JPG)

And Then if Game is Over ! ( **Human** vs **AlphaBeta** ) ( Winner is me :D ) The yellow pieces are **Kings**
![3](https://user-images.githubusercontent.com/38104305/104843980-bd283180-58cd-11eb-8bb5-596d458a5099.JPG)

## TODO
JavaFX


