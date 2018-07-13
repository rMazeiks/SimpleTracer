Each number is the time for 4 iterations, in milliseconds. Multiple 4-iteration samples were taken to show deviations. using 1.png
9499, 9409, 9114, 8632, 8273, 9159

Created intersectionRow system
Instead of looping through all of the segments to determine whether a point is on a surface, a "cache" is created of all the intersections on a certain row. Huge optimisation,  about 5x faster:
1692, 1676, 1619, 1551, 1483, 1559

Removed unnecessary Point2D object creation, replaced with double x, double y. No visible effect on the benchmarks:
1779,  1762, 1744, 1710, 1497, 1644

In class geometry.Polygon, created a cache for edge calculation. I hope this is not something the Java optimizers were doing already.
1787, 1725, 1685, 1612, 2292, 1663
It's hard to tell. Let's benchmark a more complicated image with more islands,  6.png:
Before
6607, 6417, 6614, 6159, 6125, 6278
After:
6568, 6273, 6555, 6555, 6190, 6122
That is probably not statistically significant.
(back to 1.png now)


Ran profiler. Apparently, isNearBorder and getColor are taking a lot of time.
1783, 1734, 1903, 2113, 1980, 1989

Reading pixels in batches (by line) instead of individually
1548, 1558, 1558, 1695, 1551, 2186

Holly shit (excuse me!). The isNearBorder method was taking a huge amount of time. Now, it is called only after other checks (that are faster) have been performed. Good results, because the method is called much less often.

