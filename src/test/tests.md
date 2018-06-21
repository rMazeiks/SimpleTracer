passes most tests.

OK:
- basic tracing (1)
- slight gradients (2)
- subjects that pass over the border(3)
- entirely black images(5)
- Narrow tunnels concavity (7)

fails:
- jumps over thin lines (4). this kills performance, because each new loop created is checked for intersections. Also, quality goes down.
- Struggles with much more complicated subjects(6). While the individual traces are fine, finding the next tracing point becomes much slower. This happens even if the segment length is dramatically increased

