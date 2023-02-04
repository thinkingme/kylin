<#assign base = request.contextPath />
<!doctype html>
<html lang="zh">
<head>
    <meta charset="UTF-8">
    <title>log_tracks</title>
    <script src="https://cdnjs.cloudflare.com/ajax/libs/jquery/3.3.1/jquery.min.js"></script>
    <style>
        *{
            margin:0;
            padding:0;
            border:0;
        }
        .track-monitor{

            background-color:orange;
        }
        .track-pad{
            background-image: url(${base}/images/QQ20211013-125041@2x.png);
            height:568px;
            width:275px;
            background-color:green;
        }
        .track-coordinate{
            background-color:purple;
            color:white;
            height:25px;
            line-height:25px;
            font-size:12px;
        }
        .track-coordinate-list{
            font-size:12px;
            width:100%;
            word-break:break-word;
        }
    </style>
    <script>
        window.addEventListener('load',function(){
            var pad = document.getElementsByClassName('track-pad')[0];
            var monitor = document.getElementsByClassName('track-monitor')[0];
            var coordinate = document.getElementsByClassName('track-coordinate')[0];
            var clist = document.getElementsByClassName('track-coordinate-list')[0];
            var reset = document.getElementsByTagName('button')[0];
            var context = monitor.getContext('2d');
            var cset = [];
            var startx = 0, starty = 0;
            $('div').mousedown(mouseState).mouseup(mouseState);
            function fixSize(){monitor.width = window.innerWidth;};
            function log(e){
                if(cset.length == 0){
                    context.moveTo(e.x,e.y);
                }else{
                    context.strokeStyle = 'white';
                    context.lineTo(e.x,e.y);
                    context.stroke();
                }
                if(e.x-startx == e.x && e.y-starty == e.y){
                    startx = e.x;
                    starty = e.y;
                }
                coordinate.innerHTML = '(' + (e.x-startx)+', '+(e.y-starty) + ')';
                cset.push(coordinate.innerHTML);
                clist.innerHTML = cset.join(', ');
            }
            function mouseState(e) {
                if (e.type == "mouseup") {
                    $('#logs').append('<br/>'+cset.join(', '));
                    clist.innerHTML = cset.join('');
                    $.ajax({
                        type: "POST",
                        url: "/recordMock",
                        async: false,
                        data: {
                            tracks: cset.join('|')
                        },
                    });
                    cset = [];
                    pad.removeEventListener("mousemove", log);

                }
                if (e.type == "mousedown") {
                    startx = 0; starty = 0;
                    pad.addEventListener('mousemove',log);
                }
            }

            reset.addEventListener('click',function(){
                fixSize();
                cset = [];
                clist.innerHTML = '';
                coordinate.innerHTML='在绿色的方块中滑动鼠标';
            });

            fixSize();
        });
    </script>
</head>
<body>
<div class="stage">
    <div class="track-pad"></div>
    <canvas width="100" height="200" class="track-monitor"></canvas>
    <div class="track-coordinate">在绿色的方块中滑动鼠标</div>
    <button>重置</button>
    <div>
        <div id="logs"></div>
        <div class="track-coordinate-list"></div>
    </div>
</div>
</body>
</html>