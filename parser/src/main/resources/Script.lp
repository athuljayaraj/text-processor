folder('/tmp/') -> nameFilter('regex')-> createLink('/home/dk/')

inputFolder('home/dk')

line->split(',') => slices
slices -> element(2) -> split('|') -> element(1) => aon
line->split('*') => newslices
newslices -> element(1) -> split('*') -> element(1) => mou

[aon,mou]  -> store('/tmp/out/script.out')
currentFile -> delete()