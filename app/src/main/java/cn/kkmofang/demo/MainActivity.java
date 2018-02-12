package cn.kkmofang.demo;

import android.app.Activity;
import android.graphics.Point;
import android.graphics.drawable.shapes.Shape;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;

import java.util.ArrayList;
import java.util.List;

import cn.kkmofang.view.DocumentView;
import cn.kkmofang.view.Element;
import cn.kkmofang.view.ElementView;
import cn.kkmofang.view.PagerElement;
import cn.kkmofang.view.ScrollElement;
import cn.kkmofang.view.ViewContext;
import cn.kkmofang.view.ViewElement;
import cn.kkmofang.view.value.Orientation;
import cn.kkmofang.view.value.Pixel;

public class MainActivity extends Activity {
    private static final String TAG = "MainActivity";
    private Orientation orientation = Orientation.VERTICAL;
//    private ScrollElement scrollElement;
//    private List<Element> elementList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
            setContentView(R.layout.activity_main);

        Point size = new Point();
        getWindowManager().getDefaultDisplay().getSize(size);

        Pixel.UnitRPX = size.x / 750.0f;

        ViewContext viewContext = new ViewContext(getApplicationContext());

        ViewContext.push(viewContext);



        DocumentView documentView = (DocumentView) findViewById(R.id.documentView);

        ViewElement root = new ViewElement();

        root.setAttrs("width","100%","height","100%","background-color","#ddd");

        {
            ScrollElement scrollElement = new ScrollElement();
            scrollElement.setAttrs("width","100%","height","100%","background-color","#f00", "scroll", "overflow-y");
            {
                PagerElement pagerElement = new PagerElement();
                pagerElement.setAttrs("width", "100%", "height", "50%", "background-color", "#000");

                {
                    ViewElement element = new ViewElement();
                    element.setAttrs("width","100%","height","100%","background-color","#999");
                    pagerElement.append(element);

                    ViewElement element1 = new ViewElement();
                    element1.setAttrs("width","100%","height","100%","background-color","#555");
                    pagerElement.append(element1);

                    ViewElement element2 = new ViewElement();
                    element2.setAttrs("width","100%","height","100%","background-color","#999");
                    pagerElement.append(element2);

                }
                scrollElement.append(pagerElement);
            }

            {
                ViewElement element = new ViewElement();
                element.setAttrs("width","100%","height","50%","background-color","#1f0","left","50px", "right", "100px");
                {
                    ViewElement element1 = new ViewElement();
                    element1.setAttrs("width","50%","height","50%","background-color","#000","left","50px", "right", "100px");
                    element.append(element1);
                }
                scrollElement.append(element);
            }

            {
                ScrollElement hScrollElement = new ScrollElement();
                hScrollElement.setAttrs("width", "100%", "height", "30%", "background-color", "#fff", "scroll", "overflow-x");
                {
                    ViewElement element = new ViewElement();
                    element.setAttrs("width", "50%", "height", "100%", "background-color", "#444");
                    hScrollElement.append(element);
                }

                {
                    ViewElement element = new ViewElement();
                    element.setAttrs("width", "50%", "height", "100%", "background-color", "#888");
                    hScrollElement.append(element);
                }
                {
                    ViewElement element = new ViewElement();
                    element.setAttrs("width", "50%", "height", "100%", "background-color", "#666");
                    hScrollElement.append(element);
                }

                scrollElement.append(hScrollElement);
            }

            {
                ScrollElement vScrollElement = new ScrollElement();
                vScrollElement.setAttrs("width", "100%", "height", "50%", "background-color", "#ff0", "scroll", "overflow-y");
                {
                    ViewElement element = new ViewElement();
                    element.setAttrs("width", "50%", "height", "50%", "background-color", "#763");
                    vScrollElement.append(element);
                }

                {
                    ViewElement element = new ViewElement();
                    element.setAttrs("width", "50%", "height", "50%", "background-color", "#888");
                    vScrollElement.append(element);
                }
                {
                    ViewElement element = new ViewElement();
                    element.setAttrs("width", "50%", "height", "50%", "background-color", "#666");
                    vScrollElement.append(element);
                }

                scrollElement.append(vScrollElement);
            }

            {
                ViewElement element = new ViewElement();
                element.setAttrs("width","100%","height","50%","background-color","#000","left","50px", "right", "100px");
                scrollElement.append(element);
            }

            {
                ViewElement element = new ViewElement();
                element.setAttrs("width","100%","height","50%","background-color","#1f0","left","50px", "right", "100px");
                scrollElement.append(element);
            }
            root.append(scrollElement);
        }


//        PagerElement pagerElement = new PagerElement();
//        pagerElement.setAttrs("width", "100%", "height", "50%", "background-color", "#000");
//
//        {
//            ViewElement element = new ViewElement();
//            element.setAttrs("width","100%","height","100%","background-color","#999");
//            pagerElement.append(element);
//
//            ViewElement element1 = new ViewElement();
//            element1.setAttrs("width","100%","height","100%","background-color","#555");
//            pagerElement.append(element1);
//
//            ViewElement element2 = new ViewElement();
//            element2.setAttrs("width","100%","height","100%","background-color","#fff");
//            pagerElement.append(element2);
//
//            root.append(pagerElement);
//
//        }

        documentView.setElement(root);


        ViewContext.pop();

    }
}
