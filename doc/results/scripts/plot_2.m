nginx = [414, 804, 1179, 13386]
dUta = [388, 783, 1162, 2363]
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
