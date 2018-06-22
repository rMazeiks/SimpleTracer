passes most tests.

OK:
- basic tracing (1)
- slight gradients (2)
- subjects that pass over the border(3)
- entirely black images(5)
- Narrow tunnels concavity (7)
- Complicated objects with many "islands"

fails:
- jumps over thin lines (4). this kills performance, because each new loop created is checked for intersections. Also, quality goes down.
- Creates artifacts in some small islands with sharp edges

