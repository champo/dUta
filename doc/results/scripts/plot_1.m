nginx = [1980, 3752, 5565, 7067, 8417, 8097, 8997, 10069, 10784, 11142, 8357]
dUta = [1647, 1683, 1656, 1596, 1593, 1471, 1545, 1547, 1479, 1491, 1368]
labels = ["20 x 100", "40 x 100", "60 x 100", "80 x 100", "100 x 100", "120 x 100", "140 x 100", "160 x 100", "180 x 100", "200 x 100", "2000 x 20"]
range = [1 : size(nginx)(2)]

hold on;
plot(range, nginx, "1;nginx;");
hold on;
plot(range, dUta, "3;dUta;");

xlabel("clients x requests");
ylabel("requests/s");

#for n = range
#    xlabel(n, labels(n));
#endfor

hold off;
replot();
print -dpng plot.png;
hold off;
clearplot();
