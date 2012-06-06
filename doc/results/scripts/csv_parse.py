f = open('../stressed.csv','r')

f.readline()

res = { 'nginx':[], 'dUta':[], 'label':[] }

while True:

    l = f.readline()

    c = l.split(',')

    if len(l) == 0 or c[0] == 'TOTAL':
        break

    which = c[0].split('-')[0].strip()
    test  = c[0].split('-')[1].strip()
    value = int(float(c[7].strip()))

    if which == 'nging':
        which = 'nginx'
        test = test.split('(')[1].split(')')[0]

    res[which] += [value]

    if which == 'nginx':
        res['label'] += [test]

print res
