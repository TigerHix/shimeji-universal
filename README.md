# shimeji-universal

An universal Windows version of [shimeji-ee](https://code.google.com/p/shimeji-ee/), supporting both 32-bit and 64-bit Windows.

shimeji-universal is based on [Kilkakon's fork](http://kilkakon.com/projects/shimeji.php) of the original project.

### Why you made this?

I play [Undertale](http://store.steampowered.com/app/391540/), and have found [a shimeji](http://pkbunny.tumblr.com/) of my favourate character this morning. I got so excited and had immediately downloaded it, and it turns out that shimeji does not work properly with my 64-bit machine. So I fixed it.

### How did you fix it?

By changing a numerical value. Literally. Search `getBitmapSize()` and you will see it.
