Each number is the time for 4 iterations, in milliseconds. Multiple 4-iteration samples were taken to show deviations. using 1.png
9499, 9409, 9114, 8632, 8273, 9159

Created intersectionRow system
Instead of looping through all of the segments to determine whether a point is on a surface, a "cache" is created of all the intersections on a certain row. Huge optimisation,  about 5x faster:
1692, 1676, 1619, 1551, 1483, 1559

Removed unnecessary Point2D object creation, replaced with double x, double y. No visible effect on the benchmarks:
1779,  1762, 1744, 1710, 1497, 1644

In class Outline, created a cache for edge calculation. I hope this is not something the Java optimizators were doing already.
1787, 1725, 1685, 1612, 2292, 1663
It's hard to tell. Let's benchmark a more complicated image with more islands,  6.png:
Before
6607, 6417, 6614, 6159, 6125, 6278
After:
6568, 6273, 6555, 6555, 6190, 6122
That is probably not statistically significant.
(back to 1.png now)

